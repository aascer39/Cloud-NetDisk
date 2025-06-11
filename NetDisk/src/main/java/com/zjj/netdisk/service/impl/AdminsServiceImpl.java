package com.zjj.netdisk.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.AdminsDTO;
import com.zjj.netdisk.mapper.AdminsMapper;
import com.zjj.netdisk.pojo.ApiResult;
import com.zjj.netdisk.pojo.LoginDTO;
import com.zjj.netdisk.pojo.TokenVO;
import com.zjj.netdisk.satoken.StpKit;
import com.zjj.netdisk.service.AdminsService;
import com.zjj.netdisk.utils.UtilityTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 34978
 */
@Service
public class AdminsServiceImpl extends ServiceImpl<AdminsMapper, AdminsDTO>
        implements AdminsService {

    private final AdminsMapper adminsMapper;

    @Autowired
    public AdminsServiceImpl(AdminsMapper adminsMapper) {
        this.adminsMapper = adminsMapper;
    }

    @Override
    public ApiResult<?> login(LoginDTO loginDTO) {
        QueryWrapper<AdminsDTO> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginDTO.getUsername())
                .eq("password_hash", DigestUtil.sha256Hex(loginDTO.getPassword()));

        AdminsDTO admin = baseMapper.selectOne(wrapper);

        if (admin == null) {
            return ApiResult.error(404, "用户名或密码错误");
        }

        if ("suspended".equals(admin.getStatus())) {
            return ApiResult.error(403, "管理员已被禁用");
        } else if ("deleted".equals(admin.getStatus())) {
            return ApiResult.error(410, "管理员已被删除");
        }
        // 登录成功，设置会话
        try {
            StpKit.ADMIN.login(admin.getAdminId());
        } catch (RuntimeException e) {
            return ApiResult.error(500, "登录失败，请稍后再试");
        }

        admin.setLastLoginTs(UtilityTools.getBeijingTimestamp());
        baseMapper.updateById(admin);

        return ApiResult.success("登陆成功", TokenVO.fromAdminLoginResponse(admin, StpKit.ADMIN.getTokenInfo()));
    }
}