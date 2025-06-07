package com.zjj.netdisk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.DigestUtil;
import com.zjj.netdisk.pojo.ApiResult;
import com.zjj.netdisk.pojo.UpdatePasswordDTO;
import com.zjj.netdisk.pojo.UpdateUserDTO;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.pojo.LoginDTO;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.utils.UtilityTools;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 34978
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {
    // 假设你会用它来获取存储的用户信息
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @RequestMapping("/test")
    public ApiResult<?> test() {
        UsersDTO user = usersService.getById(1018);
        return ApiResult.success("返回数据", user);
    }

    // 会话登录接口
    @Operation(summary = "用户登录")
    @PostMapping("/session")
    public ApiResult<?> doLogin(@RequestBody LoginDTO loginDTO) {
        return usersService.login(loginDTO);
    }

    // 会话登出接口
    @Operation(summary = "用户登出")
    @PostMapping("/doLogout")
    public ApiResult<?> doLogout() {
        try {
            // 直接登出
            StpUtil.logout();
            return ApiResult.success("登出成功", null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @Operation(summary = "用户注册")
    @RequestMapping("/register")
    public SaResult registerUser(String username, String password, String email) {
        // 检查用户名是否已存在
        UsersDTO existingNameUser = usersService.selectByUsername(username);
        if (existingNameUser != null) {
            return SaResult.error("用户名已存在");
        }
        UsersDTO existingEmailUser = usersService.selectByEmail(email);
        if (existingEmailUser != null) {
            return SaResult.error("邮箱已存在");
        }

        // 创建新用户
        UsersDTO newUser = UsersDTO.builder()
                .username(username)
                .passwordHash(DigestUtil.sha256Hex(password))
                // 可选，设置邮箱
                .email(email)
                .registrationTs(UtilityTools.getBeijingTimestamp())
                .lastLoginTs(UtilityTools.getBeijingTimestamp())
                // 设置状态为活跃
                .status("active")
                // 1GB 存储配额
                .storageQuotaBytes(1073741824L)
                // 初始使用空间为0
                .usedStorageBytes(0L)
                // 默认不是管理员
                .isAdmin(0)
                .build();
        // 插入新用户到数据库
        try {
            usersService.insertUser(newUser);
        } catch (Exception e) {
            return SaResult.error(e.getMessage());
        }
        // 分配用户ID

        Long userId = usersService.selectByUsername(username).getUserId();
        log.info("新用户注册成功，用户ID: {}", userId);
        return SaResult.ok("用户注册成功");
    }

    //    更新用户信息
    @Operation(summary = "更新用户信息")
    @PutMapping("/updateUser")
    @SaCheckLogin
    public SaResult updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        usersService.updateUser(userId, updateUserDTO);
        return SaResult.ok("用户信息更新成功");
    }

    //    检查是否是本人接口
    @Operation(summary = "检查是否是本人")
    @RequestMapping("/checkIsMe")
    public SaResult checkIsMe(String oldPwd) {
        // 检查旧密码是否正确
        UsersDTO user = usersService.getById(StpUtil.getLoginIdAsLong());
        String hashPwd = user.getPasswordHash();
        String inputOldPasswordHash = DigestUtil.sha256Hex(oldPwd);
        if (!hashPwd.equals(inputOldPasswordHash)) {
            return SaResult.error("旧密码错误");
        }
        return SaResult.ok("验证成功，您是本人");
    }

    //    修改密码接口
    @Operation(summary = "修改密码")
    @RequestMapping("/updatePassword")
    @SaCheckLogin
    public ApiResult<?> updatePassword(@RequestBody UpdatePasswordDTO passwordDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        return usersService.updatePassword(userId, passwordDTO);
    }

    // 用户自己注销接口
    @Operation(summary = "注销")
    @RequestMapping("/deleteUser")
    public ApiResult<?> deleteUser() {
        Long tokenUserId = StpUtil.getLoginIdAsLong();
        // 普通用户，删除这个用户
        StpUtil.logout();
        usersService.deleteUser(tokenUserId);
        return ApiResult.success("用户注销成功", null);
    }
}