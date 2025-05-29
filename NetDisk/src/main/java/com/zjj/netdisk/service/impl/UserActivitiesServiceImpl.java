package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.UserActivities;
import com.zjj.netdisk.service.UserActivitiesService;
import com.zjj.netdisk.mapper.UserActivitiesMapper;
import org.springframework.stereotype.Service;

/**
* @author 34978
* @description 针对表【user_activities(用户活动日志表)】的数据库操作Service实现
* @createDate 2025-05-29 13:53:22
*/
@Service
public class UserActivitiesServiceImpl extends ServiceImpl<UserActivitiesMapper, UserActivities>
    implements UserActivitiesService{

}




