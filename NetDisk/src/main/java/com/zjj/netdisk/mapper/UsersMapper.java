package com.zjj.netdisk.mapper;

import com.zjj.netdisk.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 34978
* @description 针对表【users(用户信息表)】的数据库操作Mapper
* @createDate 2025-05-31 00:35:03
* @Entity com.zjj.netdisk.entity.Users
*/
public interface UsersMapper extends BaseMapper<Users> {
    Users selectByUsername(String username);
    Users selectByEmail(String username);
    void insertUser(Users user);
    void updateUser(Users user);
    void deleteUser(Long userId);
}




