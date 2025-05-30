package com.zjj.netdisk.controller;

// 导入 SaSecureUtil
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.DigestUtil;
import com.zjj.netdisk.entity.Users;
import com.zjj.netdisk.mapper.UsersMapper;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.utils.UtilityTools;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Objects;

/**
 * @author 34978
 */
@Controller
@RequestMapping("/miku")
public class UsersController {
    // 假设你会用它来获取存储的用户信息
    @Autowired
    private UsersService usersService;

    @RequestMapping("/test")
    @ResponseBody // 注意这里
    public Object test() {
        Users user = usersService.getById(1004);
        return user;
    }

    // 会话登录接口
    @Operation(summary = "用户登录接口")
    @RequestMapping("/doLogin")
    @ResponseBody
    public SaResult doLogin(String name, String pwd) {
        Users user = usersService.selectByUsername(name);
        if (user == null) {
            return SaResult.error("用户不存在");
        }
        String hashPwd = user.getPasswordHash();
        // 首先检查用户名
        if (user.getUsername().equals(name)) {
            // 第一步：将用户提交的密码进行哈希处理
            String inputPasswordHash = DigestUtil.sha256Hex(pwd);

            // 第二步：比较哈希后的输入密码与存储的哈希密码
            if (hashPwd.equals(inputPasswordHash)) {
                // 如果密码匹配，更新用户的最后登录时间戳
                user.setLastLoginTs(UtilityTools.getBeijingTimestamp());
                // 更新用户信息
                usersService.updateUser(user);
                // 进行登录，分发token
                StpUtil.login(user.getUserId());
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return SaResult.data(tokenInfo);
            }
        }
        return SaResult.error("登录失败：用户名或密码错误");
    }

    // 会话登出接口
    @Operation(summary = "用户登出接口")
    @RequestMapping("/doLogout")
    @ResponseBody
    public SaResult doLogout() {
        // 直接登出
        StpUtil.logout();
        return SaResult.ok("登出成功");
    }

    @Operation(summary = "用户注册接口")
    @RequestMapping("/register")
    @ResponseBody
    public SaResult registerUser(String username, String password, String email) {
        // 检查用户名是否已存在
        Users existingUser = usersService.selectByUsername(username);
        if (existingUser != null) {
            return SaResult.error("用户名已存在");
        }

        // 创建新用户
        Users newUser = new Users();
        newUser.setUsername(username);
        // 哈希密码
        newUser.setPasswordHash(SaSecureUtil.sha256(password));
        // 可选，设置邮箱
        newUser.setEmail(email);
        // 设置注册时间戳
        newUser.setRegistrationTs(UtilityTools.getBeijingTimestamp());
        // 设置最后登录时间戳
        newUser.setLastLoginTs(UtilityTools.getBeijingTimestamp());
        // 设置状态为活跃
        newUser.setStatus("active");
        // 1GB 存储配额
        newUser.setStorageQuotaBytes(1073741824L);
        // 初始使用空间为0
        newUser.setUsedStorageBytes(0L);
        // 默认不是管理员
        newUser.setIsAdmin(0);

        // 插入新用户到数据库
        usersService.insertUser(newUser);

        return SaResult.ok("注册成功");
    }

    //    更新用户信息
    @Operation(summary = "更新用户信息接口")
    @RequestMapping("/updateUser")
    @ResponseBody
    public SaResult updateUser(String username, String email) {
        // 检查用户是否存在
        Long tokenUserId = StpUtil.getLoginIdAsLong();
        Users user = usersService.getById(tokenUserId);
        if (user == null) {
            return SaResult.error("用户不存在");
        }

//        判断是否存在相同的用户名和邮箱
        if ((usersService.selectByUsername(username) != null) && (!Objects.equals(usersService.selectByUsername(username).getUsername(), username))) {
            return SaResult.error("用户名已存在");
        } else if ((usersService.selectByEmail(email) != null) && (!Objects.equals(usersService.selectByEmail(email).getEmail(), email))) {
            return SaResult.error("邮箱已存在");
        } else {
            user.setUsername(username);
            user.setEmail(email);
        }
        // 更新用户信息
        usersService.updateUser(user);

        return SaResult.ok("用户信息更新成功");
    }

//    检查是否是本人接口
    @Operation(summary = "检查是否是本人接口")
    @RequestMapping("/checkIsMe")
    @ResponseBody
    public SaResult checkIsMe(String oldPwd) {
        // 检查旧密码是否正确
        Users user = usersService.getById(StpUtil.getLoginIdAsLong());
        String hashPwd = user.getPasswordHash();
        String inputOldPasswordHash = DigestUtil.sha256Hex(oldPwd);
        if (!hashPwd.equals(inputOldPasswordHash)) {
            return SaResult.error("旧密码错误");
        }
        return SaResult.ok("验证成功，您是本人");
    }

//    修改密码接口
    @Operation(summary = "修改密码接口")
    @RequestMapping("/updatePassword")
    @ResponseBody
    public SaResult updatePassword(String newPassword) {

        Users user = usersService.getById(StpUtil.getLoginIdAsLong());
        if(user.getPasswordHash().equals(DigestUtil.sha256Hex(newPassword))){
            return SaResult.error("你的新密码与旧密码相同，请重新输入");
        }
        // 更新密码
        user.setPasswordHash(SaSecureUtil.sha256(newPassword));
        user.setLastPasswordUpdateTs(UtilityTools.getBeijingTimestamp());
        usersService.updateUser(user);

        return SaResult.ok("密码修改成功");
    }

    // 用户自己注销接口
    @Operation(summary = "注销接口")
    @RequestMapping("/deleteUser")
    @ResponseBody
    public SaResult deleteUser() {
        Long tokenUserId = StpUtil.getLoginIdAsLong();
        // 普通用户，删除这个用户
        StpUtil.logout();
        usersService.deleteUser(tokenUserId);
        return SaResult.ok("您的账号已被注销");
    }
}