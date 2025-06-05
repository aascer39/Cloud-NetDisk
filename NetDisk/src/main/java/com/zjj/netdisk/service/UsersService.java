package com.zjj.netdisk.service;

import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 34978
* @description 针对表【users(用户信息表)】的数据库操作Service
* @createDate 2025-05-31 00:35:03
*/
public interface UsersService extends IService<UsersDTO> {
    UsersDTO selectByUsername(String username);
    UsersDTO selectByEmail(String email);
    UsersDTO selectByUserId(Long userId);
    void insertUser(UsersDTO user);
    void updateUser(UsersDTO user);
    void deleteUser(Long userId);
}
