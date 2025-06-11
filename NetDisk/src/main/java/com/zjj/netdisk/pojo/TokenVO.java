package com.zjj.netdisk.pojo;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zjj.netdisk.entity.DTO.AdminsDTO;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.utils.UtilityTools;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author 34978
 */
@Data
@Builder
public class TokenVO {
    private String tokenName;

    private String tokenValue;

    private Long loginId;

    private String username;

    private String status;

    private Integer isAdmin;

    private Long usedStorageBytes;

    private Long storageQuotaBytes;

    private String loginType;

    private String loginDeviceType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTs;


    // 一个静态方法用于从 UsersDTO 和 SaResult 转换成json
    public static TokenVO fromUserLoginResponse(UsersDTO user, SaTokenInfo saTokenInfo) {
        return TokenVO.builder()
                .tokenName(saTokenInfo.getTokenName())
                .tokenValue(saTokenInfo.getTokenValue())
                .loginId(user.getUserId())
                .username(user.getUsername())
                .status(user.getStatus().toString())
                .usedStorageBytes(user.getUsedStorageBytes())
                .storageQuotaBytes(user.getStorageQuotaBytes())
                .loginType(saTokenInfo.getLoginType())
                .loginDeviceType(saTokenInfo.getLoginDeviceType())
                .lastLoginTs(UtilityTools.getBeijingTimestamp())
                .build();
    }

    public static TokenVO fromAdminLoginResponse(AdminsDTO admin, SaTokenInfo saTokenInfo) {
        return TokenVO.builder()
                .tokenName(saTokenInfo.getTokenName())
                .tokenValue(saTokenInfo.getTokenValue())
                .loginId(admin.getAdminId())
                .username(admin.getUsername())
                .status(admin.getStatus().toString())
                .loginType(saTokenInfo.getLoginType())
                .loginDeviceType(saTokenInfo.getLoginDeviceType())
                .lastLoginTs(UtilityTools.getBeijingTimestamp())
                .build();
    }
}