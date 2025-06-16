package com.zjj.netdisk.pojo.response;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zjj.netdisk.entity.DTO.Users;
import com.zjj.netdisk.utils.UtilityTools;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author 34978
 */
@Data
@Builder
public class UserLoginResponse {
    private String tokenName;

    private String tokenValue;

    private Long loginId;

    private String username;

    private String status;


    private Long usedStorageBytes;

    private Long storageQuotaBytes;

    private String loginType;

    private String loginDeviceType;

    private String records;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTs;


    // 一个静态方法用于从 UsersDTO 和 SaResult 转换成json
    public static UserLoginResponse fromUserLoginResponse(Users user, SaTokenInfo saTokenInfo) {
        return UserLoginResponse.builder()
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
}