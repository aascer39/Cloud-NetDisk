package com.zjj.netdisk.mapper;

import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 34978
* @description 针对表【users(用户信息表)】的数据库操作Mapper
* @createDate 2025-05-31 00:35:03
* @Entity com.zjj.netdisk.entity.DTO.UsersDTO
*/
public interface UsersMapper extends BaseMapper<UsersDTO> {
    UsersDTO selectByUsername(String username);
    UsersDTO selectByEmail(String username);
    UsersDTO selectByUserId(Long userId);
    void insertUser(UsersDTO user);
    void updateUser(UsersDTO user);
    void deleteUser(Long userId);
}




