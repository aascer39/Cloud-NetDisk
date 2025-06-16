package com.zjj.netdisk.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.netdisk.entity.DTO.Admins;
import com.zjj.netdisk.mapper.AdminsMapper;
import com.zjj.netdisk.pojo.response.GlobalResponse;
import com.zjj.netdisk.pojo.LoginDTO;
import com.zjj.netdisk.pojo.response.AdminLoginResponse;
import com.zjj.netdisk.satoken.StpKit;
import com.zjj.netdisk.service.AdminsService;
import com.zjj.netdisk.utils.UtilityTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author 34978
 */
@Service
public class AdminsServiceImpl extends ServiceImpl<AdminsMapper, Admins>
        implements AdminsService {

    private final AdminsMapper adminsMapper;

    @Autowired
    public AdminsServiceImpl(AdminsMapper adminsMapper) {
        this.adminsMapper = adminsMapper;
    }

    @Override
    @Cacheable(value = "adminsLogin", key = "#loginDTO.username")
    public GlobalResponse<?> login(LoginDTO loginDTO) {
        QueryWrapper<Admins> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginDTO.getUsername())
                .eq("password_hash", DigestUtil.sha256Hex(loginDTO.getPassword()));

        Admins admin = baseMapper.selectOne(wrapper);

        if (admin == null) {
            return GlobalResponse.error(404, "用户名或密码错误");
        }

        if ("suspended".equals(admin.getStatus())) {
            return GlobalResponse.error(403, "管理员已被禁用");
        } else if ("deleted".equals(admin.getStatus())) {
            return GlobalResponse.error(410, "管理员已被删除");
        }
        // 登录成功，设置会话
        try {
            StpKit.ADMIN.login(admin.getAdminId());
        } catch (RuntimeException e) {
            return GlobalResponse.error(500, "登录失败，请稍后再试");
        }

        admin.setLastLoginTs(UtilityTools.getBeijingTimestamp());
        baseMapper.updateById(admin);

        return GlobalResponse.success("登陆成功", AdminLoginResponse.fromAdminLoginResponse(admin, StpKit.ADMIN.getTokenInfo()));
    }

    @Override
    @CacheEvict(value = {"adminsLogin", "adminInfo"}, allEntries = true)
    public GlobalResponse<?> logout() {
        try {
            StpKit.ADMIN.logout();
            return GlobalResponse.success("登出成功");
        } catch (Exception e) {
            return GlobalResponse.error(500, "登出失败，请稍后再试");
        }
    }

    @Override
    @Cacheable(value = "adminInfo", key = "#adminId")
    public GlobalResponse<?> getAdminInfo(Long adminId) {
        QueryWrapper<Admins> wrapper = new QueryWrapper<>();
        wrapper.eq("admin_id", adminId);
        wrapper.select("admin_id", "username", "last_login_ts", "email", "role", "status");
        Admins admin = adminsMapper.selectOne(wrapper);
        if (admin == null) {
            return GlobalResponse.error(404, "管理员不存在");
        }
        return GlobalResponse.success("获取管理员信息成功", admin);
    }

}