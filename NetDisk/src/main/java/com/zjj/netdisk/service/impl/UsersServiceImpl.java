package com.zjj.netdisk.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.pojo.ApiResult;
import com.zjj.netdisk.pojo.UpdatePasswordDTO;
import com.zjj.netdisk.pojo.UpdateUserDTO;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.pojo.LoginDTO;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.mapper.UsersMapper;
import com.zjj.netdisk.utils.UtilityTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 34978
 * 针对表【users(用户信息表)】的数据库操作Service实现
 * 2025-05-31 00:35:03
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, UsersDTO>
        implements UsersService {
    @Override
    public UsersDTO getUserById(Long userId) {
        return baseMapper.selectById(userId);
    }


    @Override
    public UsersDTO selectByUsername(String username) {
        QueryWrapper<UsersDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void insertUser(UsersDTO user) {
        try {
            baseMapper.insert(user);
        } catch (Exception e) {
            log.error("插入用户失败：", e);
            throw new RuntimeException("插入用户失败", e);
        }
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UpdateUserDTO updateUserDTO) {
        String newUsername = updateUserDTO.getUsername();
        String newEmail = updateUserDTO.getEmail();

        // 1. 校验用户名是否已被【其他】用户占用
        if (newUsername != null && !newUsername.isEmpty()) {
            QueryWrapper<UsersDTO> usernameWrapper = new QueryWrapper<>();
            usernameWrapper.eq("username", newUsername);
            UsersDTO userWithSameName = this.getOne(usernameWrapper);
            if (userWithSameName != null && !userWithSameName.getUserId().equals(userId)) {
                // 找到了同名用户，并且这个用户不是我们当前要修改的用户
                throw new RuntimeException("用户名已被占用");
            }
        }

        // 2. 校验邮箱是否已被【其他】用户占用
        if (newEmail != null && !newEmail.isEmpty()) {
            QueryWrapper<UsersDTO> emailWrapper = new QueryWrapper<>();
            emailWrapper.eq("email", newEmail);
            UsersDTO userWithSameEmail = this.getOne(emailWrapper);
            if (userWithSameEmail != null && !userWithSameEmail.getUserId().equals(userId)) {
                // 找到了同邮用户，并且这个用户不是我们当前要修改的用户
                throw new RuntimeException("邮箱已被占用");
            }
        }

        // 3. 执行更新操作
        UsersDTO userToUpdate = this.getById(userId);
        if (userToUpdate == null) {
            throw new RuntimeException("用户不存在");
        }

        // 4. 设置新值
        if (newUsername != null && !newUsername.isEmpty()) {
            userToUpdate.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.isEmpty()) {
            userToUpdate.setEmail(newEmail);
        }

        if (updateUserDTO.getUsedStorageBytes() != null) {
            userToUpdate.setUsedStorageBytes(updateUserDTO.getUsedStorageBytes());
        }

        if (updateUserDTO.getUsedStorageBytes() != null) {
            userToUpdate.setLastLoginTs(updateUserDTO.getLastLoginTs());
        }

        // 调用BaseMapper自带的方法更新
        this.updateById(userToUpdate);
    }

    @Override
    public void deleteUser(Long userId) {
        baseMapper.deleteById(userId);
    }

    @Override
    public UsersDTO selectByEmail(String email) {
        QueryWrapper<UsersDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public ApiResult<?> updatePassword(Long userId, UpdatePasswordDTO passwordDTO) {
        UsersDTO user = baseMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }

        String oldPwdHash = DigestUtil.sha256Hex(passwordDTO.getOldPassword());
        String newPwdHash = DigestUtil.sha256Hex(passwordDTO.getNewPassword());
        if (!oldPwdHash.equals(user.getPasswordHash())) {
            return ApiResult.error(404, "旧密码错误");
        } else if (passwordDTO.getNewPassword() == null || passwordDTO.getNewPassword().isEmpty()) {
            return ApiResult.error(400, "新密码不能为空");
        } else if (newPwdHash.equals(oldPwdHash)) {
            return ApiResult.error(400, "新密码不能与旧密码相同");
        }
        user.setPasswordHash(newPwdHash);
        user.setLastPasswordUpdateTs(passwordDTO.getLastPasswordUpdateTs());
        user.setLastPasswordUpdateTs(UtilityTools.getBeijingTimestamp());
        baseMapper.updateById(user);
        return ApiResult.success("密码更新成功", null);
    }

    @Override
    public UsersDTO login(LoginDTO loginDTO) {
        QueryWrapper<UsersDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginDTO.getUsername())
                .eq("password_hash", DigestUtil.sha256Hex(loginDTO.getPassword()));
        UsersDTO user = baseMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.setLastLoginTs(UtilityTools.getBeijingTimestamp());
        baseMapper.updateById(user);
        return user;
    }

}




