package com.zjj.netdisk.mapper;

import com.zjj.netdisk.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.catalina.User;

/**
* @author 34978
* @description 针对表【users(用户信息表)】的数据库操作Mapper
* @createDate 2025-05-29 13:53:22
* @Entity com.zjj.netdisk.entity.Users
*/
public interface UsersMapper extends BaseMapper<Users> {
    Users selectByUsername(String username);
    Users selectByEmail(String username);
    void insertUser(Users user);
    void updateUser(Users user);
    void deleteUser(Long userId);
}




