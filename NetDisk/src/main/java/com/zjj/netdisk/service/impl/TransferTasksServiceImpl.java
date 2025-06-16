package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.TransferTasks;
import com.zjj.netdisk.service.TransferTasksService;
import com.zjj.netdisk.mapper.TransferTasksMapper;
import org.springframework.stereotype.Service;

/**
* @author 34978
* @description 针对表【transfer_tasks(传输任务表，支持断点续传和任务控制)】的数据库操作Service实现
* @createDate 2025-06-16 17:07:50
*/
@Service
public class TransferTasksServiceImpl extends ServiceImpl<TransferTasksMapper, TransferTasks>
    implements TransferTasksService{

}




