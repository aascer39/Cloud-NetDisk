package com.zjj.netdisk.pojo.response;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zjj.netdisk.entity.DTO.AdminsDTO;
import com.zjj.netdisk.utils.UtilityTools;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author 34978
 */
@Data
@Builder
public class AdminLoginResponse {
    private String tokenName;

    private String tokenValue;

    private Long loginId;

    private String username;

    private String status;

    private String loginType;

    private String loginDeviceType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTs;


    public static AdminLoginResponse fromAdminLoginResponse(AdminsDTO admin, SaTokenInfo saTokenInfo) {
        return AdminLoginResponse.builder()
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