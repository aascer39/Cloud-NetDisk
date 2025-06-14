package com.zjj.netdisk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.crypto.digest.DigestUtil;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.pojo.*;
import com.zjj.netdisk.satoken.StpKit;
import com.zjj.netdisk.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 34978
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "用户接口")
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
            StpKit.USER.logout();
            return ApiResult.success("登出成功", null);
        } catch (Exception e) {
            return ApiResult.error(500, e.getMessage());
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public ApiResult<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.isNeedHashed()) {
            registerRequest.setPassword(DigestUtil.sha256Hex(registerRequest.getPassword()));
        }
        return usersService.registerUser(registerRequest);
    }

    //    更新用户信息
    @Operation(summary = "更新用户信息")
    @PutMapping("/updateUser")
    @SaCheckLogin(type = "user,admin")
    public SaResult updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        usersService.updateUser(updateUserDTO.getUserId(), updateUserDTO);
        return SaResult.ok("用户信息更新成功");
    }

    //    修改密码接口
    @Operation(summary = "修改密码")
    @PatchMapping("/updatePassword")
    @SaCheckLogin(type = "user")
    public ApiResult<?> updatePassword(@RequestBody UpdatePasswordDTO passwordDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        return usersService.updatePassword(userId, passwordDTO);
    }

    // 用户自己注销接口
    @Operation(summary = "删除用户")
    @RequestMapping("/deleteUser/{userId}")
    @SaCheckLogin(type = "admin")
    public ApiResult<?> deleteUser(@PathVariable("userId") Long userId) {
        return usersService.deleteUser(userId);
    }
}