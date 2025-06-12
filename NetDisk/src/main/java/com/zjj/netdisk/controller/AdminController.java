package com.zjj.netdisk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.pojo.ApiResult;
import com.zjj.netdisk.pojo.LoginDTO;
import com.zjj.netdisk.pojo.PageDTO;
import com.zjj.netdisk.satoken.StpKit;
import com.zjj.netdisk.service.AdminsService;
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
    public ApiResult<?> doLogin(@RequestBody LoginDTO loginDTO) {
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
    public ApiResult<?> listUsersByPage(PageDTO pageDTO) {
        log.info("管理员 {} 正在分页查询用户列表，参数: {}", StpKit.ADMIN.getLoginId(), pageDTO);

        IPage<UsersDTO> pageResult = usersService.findUserPageWithQueryWrapper(pageDTO);

        return ApiResult.success("查询成功", pageResult);
    }

    /**
     * 管理员登出接口
     *
     * @return ApiResult
     */
    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    @SaCheckLogin(type = "admin")
    public ApiResult<?> doLogout() {
        return adminsService.logout();
    }
}