package com.zjj.netdisk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjj.netdisk.entity.DTO.Users;
import com.zjj.netdisk.pojo.AdminAddUserRequest;
import com.zjj.netdisk.pojo.response.GlobalResponse;
import com.zjj.netdisk.pojo.LoginDTO;
import com.zjj.netdisk.pojo.PageDTO;
import com.zjj.netdisk.satoken.StpKit;
import com.zjj.netdisk.service.AdminsService;
import com.zjj.netdisk.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

/**
 * @author 34978
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "管理员接口")
public class AdminController {
    // 假设你会用它来获取存储的用户信息
    private final AdminsService adminsService;
    private final UsersService usersService;

    @Autowired
    public AdminController(AdminsService adminsService, UsersService usersService) {
        this.adminsService = adminsService;
        this.usersService = usersService;
    }

    // 会话登录接口
    @Operation(summary = "管理员登录")
    @PostMapping("/session")
    public GlobalResponse<?> doLogin(@RequestBody LoginDTO loginDTO) {
        return adminsService.login(loginDTO);
    }

    /**
     * (已修改) 管理员分页获取所有用户列表的接口
     *
     * @param pageDTO 包含分页参数（current, size）
     * @return 带分页信息的用户列表
     */
    @Operation(summary = "[管理员] 分页获取用户列表")
    @GetMapping("/page")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> listUsersByPage(PageDTO pageDTO) {
        log.info("管理员 {} 正在从Redis中分页查询用户列表，参数: {}", StpKit.ADMIN.getLoginId(), pageDTO);
        IPage<Users> pageResult = usersService.findUserPageWithQueryWrapper(pageDTO);

        return GlobalResponse.success("查询成功", pageResult);
    }

    /**
     * 管理员登出接口
     *
     * @return GlobalResponse
     */
    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public GlobalResponse<?> doLogout() {
        return adminsService.logout();
    }

    //    管理员添加新的用户
    @Operation(summary = "管理员添加新用户")
    @PostMapping("/addUser")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> addUser(@RequestBody AdminAddUserRequest request) {
        return usersService.adminAddUser(request.getUsername(), request.getEmail());
    }

    // 封禁用户
    @Operation(summary = "封禁用户")
    @PatchMapping("/suspendUser/{userId}")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> suspendUser(@PathVariable("userId") Long userId) {
        return usersService.suspendUser(userId);
    }

    // 解封用户
    @Operation(summary = "解封用户")
    @PatchMapping("/unsuspendUser/{userId}")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> unsuspendUser(@PathVariable("userId") Long userId) {
        return usersService.unsuspendUser(userId);
    }

    //重置登录密码
    @Operation(summary = "重置登录密码")
    @PatchMapping("/resetPassword/{userId}")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> resetPassword(@PathVariable("userId") Long userId) {
        return usersService.resetPassword(userId);
    }

    //获取管理员信息
    @Operation(summary = "获取管理员信息")
    @GetMapping("/info/{AdminId}")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> getAdminInfo(@PathVariable("AdminId") Long adminId) {
        return adminsService.getAdminInfo(adminId);
    }

    //为用户更改最大容量
    @Operation(summary = "更改最大容量")
    @PatchMapping("/updateUserStorage")
    @SaCheckLogin(type = "admin")
    public GlobalResponse<?> updateUserStorage(Long userId, Long storageLimit) {
        return usersService.updateUserStorage(userId, storageLimit);
    }
}