package com.zjj.netdisk.service;

import com.zjj.netdisk.entity.DTO.UserFilesDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 34978
* @description 针对表【user_files(用户文件/文件夹表，代表用户视角的文件和文件夹)】的数据库操作Service
* @createDate 2025-06-01 15:14:22
*/
public interface UserFilesService extends IService<UserFilesDTO> {
    void insertUserFiles(UserFilesDTO userFiles);
}
