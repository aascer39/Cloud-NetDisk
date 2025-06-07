package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.UserFilesDTO;
import com.zjj.netdisk.service.UserFilesService;
import com.zjj.netdisk.mapper.UserFilesMapper;
import org.springframework.stereotype.Service;

/**
 * @author 34978
 * @description 针对表【user_files(用户文件/文件夹表，代表用户视角的文件和文件夹)】的数据库操作Service实现
 * @createDate 2025-06-01 15:14:22
 */
@Service
public class UserFilesServiceImpl extends ServiceImpl<UserFilesMapper, UserFilesDTO>
        implements UserFilesService {
    private final UserFilesMapper userFilesMapper;

    public UserFilesServiceImpl(UserFilesMapper userFilesMapper) {
        this.userFilesMapper = userFilesMapper;
    }

    /**
     * 插入用户文件
     *
     * @param userFiles 用户文件对象
     */
    @Override
    public void insertUserFiles(UserFilesDTO userFiles) {
        baseMapper.insert(userFiles);
    }
}




