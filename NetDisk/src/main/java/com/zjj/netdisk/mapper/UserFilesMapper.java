package com.zjj.netdisk.mapper;

import com.zjj.netdisk.entity.DTO.UserFilesDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 34978
* @description 针对表【user_files(用户文件/文件夹表，代表用户视角的文件和文件夹)】的数据库操作Mapper
* @createDate 2025-06-01 15:14:22
* @Entity com.zjj.netdisk.entity.DTO.UserFilesDTO
*/
public interface UserFilesMapper extends BaseMapper<UserFilesDTO> {
    void insertUserFiles(UserFilesDTO userFiles);
}




