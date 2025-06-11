package com.zjj.netdisk.service;

import com.zjj.netdisk.entity.DTO.AdminsDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.netdisk.pojo.ApiResult;
import com.zjj.netdisk.pojo.LoginDTO;

/**
* @author 34978
* @description 针对表【admins(管理员信息表)】的数据库操作Service
* @createDate 2025-06-11 01:32:19
*/
public interface AdminsService extends IService<AdminsDTO> {

    ApiResult<?> login(LoginDTO loginDTO);

}
