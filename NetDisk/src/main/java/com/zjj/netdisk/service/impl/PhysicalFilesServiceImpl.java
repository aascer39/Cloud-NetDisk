package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.PhysicalFilesDTO;
import com.zjj.netdisk.service.PhysicalFilesService;
import com.zjj.netdisk.mapper.PhysicalFilesMapper;
import org.springframework.stereotype.Service;

/**
* @author 34978
* @description 针对表【physical_files(物理文件表，用于文件去重)】的数据库操作Service实现
* @createDate 2025-05-29 13:53:21
*/
@Service
public class PhysicalFilesServiceImpl extends ServiceImpl<PhysicalFilesMapper, PhysicalFilesDTO>
    implements PhysicalFilesService{
    private final PhysicalFilesMapper physicalFilesMapper;

    public PhysicalFilesServiceImpl(PhysicalFilesMapper physicalFilesMapper) {
        this.physicalFilesMapper = physicalFilesMapper;
    }

    @Override
    public void insertPhysicalFiles(PhysicalFilesDTO physicalFile) {
        physicalFilesMapper.insertPhysicalFiles(physicalFile);
    }

    @Override
    public PhysicalFilesDTO selectByFileHash(String fileHash) {
        return physicalFilesMapper.selectByFileHash(fileHash);
    }

    @Override
    public void updatePhysicalFiles(PhysicalFilesDTO physicalFiles) {
        physicalFilesMapper.updatePhysicalFiles(physicalFiles);
    }
}




