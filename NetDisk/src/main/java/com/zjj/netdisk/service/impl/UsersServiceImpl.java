package com.zjj.netdisk.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.pojo.*;
import com.zjj.netdisk.pojo.response.UserLoginResponse;
import com.zjj.netdisk.satoken.StpKit;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.mapper.UsersMapper;
import com.zjj.netdisk.utils.EmailHelper;
import com.zjj.netdisk.utils.UtilityTools;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 34978
 * 针对表【users(用户信息表)】的数据库操作Service实现
 * 2025-05-31 00:35:03
 */
@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, UsersDTO>
        implements UsersService {
    private final UsersMapper usersMapper;
    private final EmailHelper emailHelper;

    @Autowired
    public UsersServiceImpl(UsersMapper usersMapper,
                            EmailHelper emailHelper) {
        this.usersMapper = usersMapper;
        this.emailHelper = emailHelper;
    }

    @Override
    public UsersDTO getUserById(Long userId) {
        return baseMapper.selectById(userId);
    }


    @Override
    public UsersDTO selectByUsername(String username) {
        QueryWrapper<UsersDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void insertUser(UsersDTO user) {
        try {
            baseMapper.insert(user);
        } catch (Exception e) {
            log.error("插入用户失败：", e);
            throw new RuntimeException("插入用户失败", e);
        }
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        String newUsername = updateUserDTO.getUsername();
        String newEmail = updateUserDTO.getEmail();

        // 1. 校验用户名是否已被【其他】用户占用
        if (newUsername != null && !newUsername.isEmpty()) {
            QueryWrapper<UsersDTO> usernameWrapper = new QueryWrapper<>();
            usernameWrapper.eq("username", newUsername);
            UsersDTO userWithSameName = this.getOne(usernameWrapper);
            if (userWithSameName != null && !userWithSameName.getUserId().equals(userId)) {
                // 找到了同名用户，并且这个用户不是我们当前要修改的用户
                throw new RuntimeException("用户名已被占用");
            }
        }

        // 2. 校验邮箱是否已被【其他】用户占用
        if (newEmail != null && !newEmail.isEmpty()) {
            QueryWrapper<UsersDTO> emailWrapper = new QueryWrapper<>();
            emailWrapper.eq("email", newEmail);
            UsersDTO userWithSameEmail = this.getOne(emailWrapper);
            if (userWithSameEmail != null && !userWithSameEmail.getUserId().equals(userId)) {
                // 找到了同邮用户，并且这个用户不是我们当前要修改的用户
                throw new RuntimeException("邮箱已被占用");
            }
        }

        // 3. 执行更新操作
        UsersDTO userToUpdate = this.getById(userId);
        if (userToUpdate == null) {
            throw new RuntimeException("用户不存在");
        }

        // 4. 设置新值
        if (newUsername != null && !newUsername.isEmpty()) {
            userToUpdate.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.isEmpty()) {
            userToUpdate.setEmail(newEmail);
        }

        if (updateUserDTO.getUsedStorageBytes() != null) {
            userToUpdate.setUsedStorageBytes(updateUserDTO.getUsedStorageBytes());
        }

        if (updateUserDTO.getUsedStorageBytes() != null) {
            userToUpdate.setLastLoginTs(updateUserDTO.getLastLoginTs());
        }

        // 调用BaseMapper自带的方法更新
        this.updateById(userToUpdate);
    }

    @Override
    public ApiResult<?> deleteUser(Long userId) {
        // 执行删除操作
        int rowsAffected = baseMapper.deleteById(userId);
        if (rowsAffected != 0) {
            return ApiResult.success("用户删除成功");
        } else {
            return ApiResult.error(404, "用户不存在或已被删除");
        }
    }

    @Override
    public UsersDTO selectByEmail(String email) {
        QueryWrapper<UsersDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public ApiResult<?> updatePassword(Long userId, UpdatePasswordDTO passwordDTO) {
        UsersDTO user = baseMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }

        String oldPwdHash = passwordDTO.getOldPassword();
        String newPwdHash = passwordDTO.getNewPassword();
        if (!oldPwdHash.equals(user.getPasswordHash())) {
            return ApiResult.error(404, "旧密码错误");
        } else if (passwordDTO.getNewPassword() == null || passwordDTO.getNewPassword().isEmpty()) {
            return ApiResult.error(400, "新密码不能为空");
        } else if (newPwdHash.equals(oldPwdHash)) {
            return ApiResult.error(400, "新密码不能与旧密码相同");
        }
        user.setPasswordHash(newPwdHash);
        user.setLastPasswordUpdateTs(passwordDTO.getLastPasswordUpdateTs());
        user.setLastPasswordUpdateTs(UtilityTools.getBeijingTimestamp());
        baseMapper.updateById(user);
        return ApiResult.success("密码更新成功", null);
    }

    @Override
    public ApiResult<?> login(LoginDTO loginDTO) {
        QueryWrapper<UsersDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginDTO.getUsername())
                .eq("password_hash", DigestUtil.sha256Hex(loginDTO.getPassword()));
        UsersDTO user = baseMapper.selectOne(wrapper);
        if (user == null) {
            return ApiResult.error(404, "用户名或密码错误");
        }
        if (user.getStatus() == "suspended") {
            return ApiResult.error(403, "用户已被禁用");
        }
        try {
            StpKit.USER.login(user.getUserId());
        } catch (RuntimeException e) {
            return ApiResult.error(500, "登录失败，请稍后再试");
        }
        user.setLastLoginTs(UtilityTools.getBeijingTimestamp());
        baseMapper.updateById(user);
        return ApiResult.success("登陆成功", UserLoginResponse.fromUserLoginResponse(user, StpKit.USER.getTokenInfo()));
    }

    /**
     * [重写] 根据查询条件分页获取用户列表
     *
     * @param pageDTO 包含分页、筛选和排序信息的查询对象
     * @return 分页后的用户数据
     */
    @Override
    public IPage<UsersDTO> findUserPageWithQueryWrapper(PageDTO pageDTO) {
        // 1. 定义允许排序的字段白名单，增强安全性
        final Set<String> allowedSortFields = new HashSet<>(Arrays.asList(
                "registration_ts",
                "last_login_ts",
                "used_storage_bytes",
                "storage_quota_bytes",
                "email",
                "username"
        ));

        // 2. 创建分页对象
        Page<UsersDTO> page = new Page<>(pageDTO.getCurrent(), pageDTO.getSize());

        // 3. 构建动态查询条件 QueryWrapper
        QueryWrapper<UsersDTO> queryWrapper = new QueryWrapper<>();

        // 指定需要查询的列，避免查询不必要的字段
        queryWrapper.select(
                "user_id", "username", "email", "status",
                "used_storage_bytes", "storage_quota_bytes",
                "last_login_ts", "registration_ts"
        );

        // 动态拼接筛选条件
        // 按名称模糊查询
        if (StringUtils.isNotBlank(pageDTO.getNameKeyword())) {
            queryWrapper.like("username", pageDTO.getNameKeyword());
        }
        // 按状态精确查询
        if (pageDTO.getStatus() != null) {
            queryWrapper.eq("status", pageDTO.getStatus());
        }

        // 4. [核心修改] 动态处理排序逻辑
        String sortField = pageDTO.getSortField();
        String sortOrder = pageDTO.getSortOrder();

        // 检查前端是否传递了有效的、且在白名单内的排序字段
        if (StringUtils.isNotBlank(sortField) && allowedSortFields.contains(sortField)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 如果没有提供有效排序参数，则使用默认排序
            queryWrapper.orderByDesc("registration_ts");
        }

        // 5. 调用 BaseMapper 的 selectPage 方法执行查询
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public ApiResult<?> suspendUser(Long userId) {
        UsersDTO user = baseMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        if ("suspended".equals(user.getStatus())) {
            return ApiResult.error(400, "用户已处于禁用状态");
        }
        user.setStatus("suspended");
        baseMapper.updateById(user);
        return ApiResult.success("用户已被禁用", null);
    }

    @Override
    public ApiResult<?> unsuspendUser(Long userId) {
        UsersDTO user = baseMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        if (!"suspended".equals(user.getStatus())) {
            return ApiResult.error(400, "用户未处于禁用状态");
        }
        // 恢复为活跃状态
        user.setStatus("active");
        baseMapper.updateById(user);
        return ApiResult.success("用户已被恢复", null);
    }

    @Override
    public ApiResult<?> resetPassword(Long userId) {
        UsersDTO user = baseMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        String resetPassword = UtilityTools.generateRandomPassword();
        user.setPasswordHash(DigestUtil.sha256Hex(resetPassword));
        user.setLastPasswordUpdateTs(UtilityTools.getBeijingTimestamp());
        // 这里添加发送重置密码邮件的逻辑
        try {
            // 1. 从 resources 目录加载 HTML 模板
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/mail/reset-password-template.html");
            if (inputStream == null) {
                throw new RuntimeException("邮件模板未找到！");
            }
            String htmlTemplate = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            // 2. 替换模板中的占位符
            // 替换为您的前端登录页面地址
            String loginUrl = "http://localhost:5173/login";
            String htmlContent = htmlTemplate
                    .replace("__NEW_PASSWORD__", resetPassword)
                    .replace("__LOGIN_URL__", loginUrl);

            // 3. 调用新的方法发送HTML邮件
            String subject = "MikuNetDisk - 密码重置通知";
            emailHelper.sendHtmlEmail(subject, htmlContent, user.getEmail());

        } catch (Exception e) {
            // 处理异常，例如记录日志
            log.error("发送密码重置邮件时出错", e);
            return ApiResult.error(500, "邮件发送失败，稍后再试。");
            // 这里可以决定是否向上抛出异常
        }
        baseMapper.updateById(user);
        return ApiResult.success("密码重置成功,密码已发送至登记邮箱", null);
    }

    //    用户注册逻辑
    @Override
    public ApiResult<?> registerUser(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        UsersDTO existingNameUser = selectByUsername(registerRequest.getUsername());
        if (existingNameUser != null) {
            log.error("用户名已存在{}", existingNameUser);
            return ApiResult.error(400, "用户名已存在");
        }
        // 检查邮箱是否已存在
        UsersDTO existingEmailUser = selectByEmail(registerRequest.getEmail());
        if (existingEmailUser != null) {
            log.error("邮箱已存在{}", existingEmailUser);
            return ApiResult.error(400, "邮箱已存在");
        }

        // 创建新用户
        UsersDTO newUser = UsersDTO.builder()
                .username(registerRequest.getUsername())
                .passwordHash(registerRequest.getPassword())
                .email(registerRequest.getEmail())
                .registrationTs(UtilityTools.getBeijingTimestamp())
                .lastLoginTs(UtilityTools.getBeijingTimestamp())
                .status("active")
                .storageQuotaBytes(1073741824L)
                .usedStorageBytes(0L)
                .build();

        // 插入新用户到数据库
        try {
            insertUser(newUser);
        } catch (Exception e) {
            return ApiResult.error(500, "注册失败，请稍后再试");
        }

        Long userId = selectByUsername(registerRequest.getUsername()).getUserId();
        log.info("新用户注册成功，用户ID: {}", userId);
        return ApiResult.success("用户注册成功", null);
    }

    @Override
    public ApiResult<?> adminAddUser(String username, String email) {
        // 检查用户名是否已存在
        UsersDTO existingNameUser = selectByUsername(username);
        if (existingNameUser != null) {
            log.error("管理员添加的用户名已存在{}", existingNameUser);
            return ApiResult.error(400, "用户名已存在");
        }
        // 检查邮箱是否已存在
        UsersDTO existingEmailUser = selectByEmail(email);
        if (existingEmailUser != null) {
            log.error("管理员添加的邮箱已存在{}", existingEmailUser);
            return ApiResult.error(400, "邮箱已存在");
        }
//        生成的临时密码
        String temPassword = UtilityTools.generateRandomPassword();

        // 创建新用户
        UsersDTO newUser = UsersDTO.builder()
                .username(username)
                .email(email)
                // 存储到数据库的是哈希后的密码
                .passwordHash(DigestUtil.sha256Hex(temPassword))
                .registrationTs(UtilityTools.getBeijingTimestamp())
                .lastLoginTs(UtilityTools.getBeijingTimestamp())
                .status("active")
                // 1 GB
                .storageQuotaBytes(1073741824L)
                .usedStorageBytes(0L)
                .build();
        try {
            // 1. 从 resources 目录加载您的新HTML模板
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/mail/hello-user-template.html");
            if (inputStream == null) {
                // 抛出更明确的异常
                throw new java.io.FileNotFoundException("邮件模板 'templates/mail/hello-user-template.html' 未在resources目录中找到！");
            }
            String htmlTemplate = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            //定义所有需要替换的占位符的值
            String htmlContent = getString(htmlTemplate, newUser, temPassword);

            // 设置邮件主题并发送HTML邮件
            String subject = "欢迎加入MikuNetDisk！您的账户已成功创建";
            emailHelper.sendHtmlEmail(subject, htmlContent, newUser.getEmail());

        } catch (Exception e) {
            // 处理异常，例如记录日志
            log.error("为新用户 {} 发送欢迎邮件时出错", newUser.getUsername(), e);
            return ApiResult.error(500, "欢迎邮件发送失败，请稍后重试或联系管理员。");
            // 这里可以决定是否向上抛出异常
        }

        // 插入新用户到数据库
        try {
            insertUser(newUser);
        } catch (Exception e) {
            return ApiResult.error(500, "添加用户失败，请稍后再试");
        }

        Long userId = selectByUsername(username).getUserId();
        log.info("管理员添加新用户成功，用户ID: {}", userId);
        return ApiResult.success("用户添加成功", null);
    }

    @NotNull
    private static String getString(String htmlTemplate, UsersDTO newUser, String temPassword) {
        String loginUrl = "http://localhost:5173/login";

//        logo的url
        String logoUrl = "https://i.kfs.io/artist/global/2249729,0v2/fit/500x500.jpg";

        // 注意：这里的占位符与您新模板中的 {{...}} 完全对应
        // 发送到邮件的是临时明文密码
        return htmlTemplate
                .replace("{{username}}", newUser.getUsername())
                // 发送到邮件的是临时明文密码
                .replace("{{password}}", temPassword)
                .replace("{{login_url}}", loginUrl)
                .replace("{{logo_url}}", logoUrl);
    }
}




