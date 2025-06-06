package com.zjj.netdisk.service;

import com.zjj.netdisk.pojo.ApiResult;
import com.zjj.netdisk.pojo.UpdatePasswordDTO;
import com.zjj.netdisk.pojo.UpdateUserDTO;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjj.netdisk.pojo.LoginDTO;

/**
 * @author 34978
 * @description 针对表【users(用户信息表)】的数据库操作Service
 * @createDate 2025-05-31 00:35:03
 */
public interface UsersService extends IService<UsersDTO> {
    UsersDTO getUserById(Long userId);

    UsersDTO selectByUsername(String username);

    UsersDTO selectByEmail(String email);

    void insertUser(UsersDTO user);

    void updateUser(Long userId, UpdateUserDTO updateUserDTO);

    void deleteUser(Long userId);

    ApiResult<?> updatePassword(Long userId, UpdatePasswordDTO passwordDTO);

    UsersDTO login(LoginDTO loginDTO);
}
