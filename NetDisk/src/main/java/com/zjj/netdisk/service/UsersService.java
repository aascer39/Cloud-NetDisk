package com.zjj.netdisk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.netdisk.entity.DTO.Users;
import com.zjj.netdisk.pojo.*;
import com.zjj.netdisk.pojo.response.GlobalResponse;

/**
 * @author 34978
 * @description 针对表【users(用户信息表)】的数据库操作Service
 * @createDate 2025-06-11 11:46:05
 */
public interface UsersService extends IService<Users> {
    Users getUserById(Long userId);

    Users selectByUsername(String username);

    Users selectByEmail(String email);

    void insertUser(Users user);

    void updateUser(Long userId, UpdateUserDTO updateUserDTO);

    GlobalResponse<?> deleteUser(Long userId);

    GlobalResponse<?> updatePassword(Long userId, UpdatePasswordDTO passwordDTO);

    GlobalResponse<?> login(LoginDTO loginDTO);

    IPage<Users> findUserPageWithQueryWrapper(PageDTO pageDTO);

    GlobalResponse<?> suspendUser(Long userId);

    GlobalResponse<?> unsuspendUser(Long userId);

    GlobalResponse<?> resetPassword(Long userId);

    GlobalResponse<?> registerUser(RegisterRequest registerRequest);

    GlobalResponse<?> adminAddUser(String username, String email);

    GlobalResponse<?> updateUserStorage(Long userId, Long storageLimit);

    GlobalResponse<?> getUserInfoById(Long userId);
}
