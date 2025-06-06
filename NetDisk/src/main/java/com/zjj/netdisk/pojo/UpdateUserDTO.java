package com.zjj.netdisk.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 用于接收前端“更新用户信息”请求的数据传输对象 (DTO)
 * @author 3497
 */
@Data // 使用Lombok自动生成getter, setter, toString等方法
@Builder
public class UpdateUserDTO {

    private String username;

    private String email;

    private Long usedStorageBytes ;

    private Date lastLoginTs;
}