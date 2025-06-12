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
import com.zjj.netdisk.utils.UtilityTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 34978
 * 针对表【users(用户信息表)】的数据库操作Service实现
 * 2025-05-31 00:35:03
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, UsersDTO>
        implements UsersService {
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

        String oldPwdHash = DigestUtil.sha256Hex(passwordDTO.getOldPassword());
        String newPwdHash = DigestUtil.sha256Hex(passwordDTO.getNewPassword());
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
        } else if (user.getStatus() == "deleted") {
            return ApiResult.error(410, "用户已被删除");

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

}




