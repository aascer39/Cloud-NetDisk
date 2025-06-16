package com.zjj.netdisk.service;

import com.zjj.netdisk.entity.DTO.Admins;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.netdisk.pojo.response.GlobalResponse;
import com.zjj.netdisk.pojo.LoginDTO;

/**
 * @author 34978
 * @description 针对表【admins(管理员信息表)】的数据库操作Service
 * @createDate 2025-06-11 01:32:19
 */
public interface AdminsService extends IService<Admins> {

    GlobalResponse<?> login(LoginDTO loginDTO);

    GlobalResponse<?> logout();

    GlobalResponse<?> getAdminInfo(Long adminId);
}
