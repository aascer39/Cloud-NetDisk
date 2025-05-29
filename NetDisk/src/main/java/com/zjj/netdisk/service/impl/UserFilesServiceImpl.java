package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.UserFiles;
import com.zjj.netdisk.service.UserFilesService;
import com.zjj.netdisk.mapper.UserFilesMapper;
import org.springframework.stereotype.Service;

/**
* @author 34978
* @description 针对表【user_files(用户文件/文件夹表，代表用户视角的文件和文件夹)】的数据库操作Service实现
* @createDate 2025-05-29 13:53:22
*/
@Service
public class UserFilesServiceImpl extends ServiceImpl<UserFilesMapper, UserFiles>
    implements UserFilesService{

}




