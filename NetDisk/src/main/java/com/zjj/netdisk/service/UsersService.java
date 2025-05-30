package com.zjj.netdisk.service;

import com.zjj.netdisk.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 34978
* @description 针对表【users(用户信息表)】的数据库操作Service
* @createDate 2025-05-31 00:35:03
*/
public interface UsersService extends IService<Users> {
    Users selectByUsername(String username);
    Users selectByEmail(String username);
    void insertUser(Users user);
    void updateUser(Users user);
    void deleteUser(Long userId);
}
