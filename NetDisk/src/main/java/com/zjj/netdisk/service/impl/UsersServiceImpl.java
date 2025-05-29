package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.Users;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.mapper.UsersMapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 34978
* @description 针对表【users(用户信息表)】的数据库操作Service实现
* @createDate 2025-05-29 13:53:22
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public Users selectByUsername(String username) {
        return usersMapper.selectByUsername(username);
    }
    @Override
    public void insertUser(Users user) {
        usersMapper.insertUser(user);
    }
    @Override
    public void updateUser(Users user) {
        usersMapper.updateUser(user);
    }
    @Override
    public void deleteUser(Long userId) {
        usersMapper.deleteUser(userId);
    }
    @Override
    public Users selectByEmail(String email) {
        return usersMapper.selectByEmail(email);
    }
}




