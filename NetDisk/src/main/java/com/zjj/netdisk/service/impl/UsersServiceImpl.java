package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.Users;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
 * @author 34978
 * @description 针对表【users(用户信息表)】的数据库操作Service实现
 * @createDate 2025-05-31 00:35:03
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UsersService {

    @Override
    public Users selectByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public void insertUser(Users user) {
        try {
            baseMapper.insertUser(user);
        } catch (Exception e) {
            log.error("插入用户失败：", e);
            throw new RuntimeException("插入用户失败", e);
        }
    }

    @Override
    public void updateUser(Users user) {
        baseMapper.updateUser(user);
    }

    @Override
    public void deleteUser(Long userId) {
        baseMapper.deleteUser(userId);
    }

    @Override
    public Users selectByEmail(String email) {
        return baseMapper.selectByEmail(email);
    }

    @Override
    public Users selectByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
}




