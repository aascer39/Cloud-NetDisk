package com.zjj.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.Favorites;
import com.zjj.netdisk.service.FavoritesService;
import com.zjj.netdisk.mapper.FavoritesMapper;
import org.springframework.stereotype.Service;

/**
* @author 34978
* @description 针对表【favorites(用户收藏的文件或文件夹的关联表)】的数据库操作Service实现
* @createDate 2025-06-16 17:07:50
*/
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites>
    implements FavoritesService{

}




