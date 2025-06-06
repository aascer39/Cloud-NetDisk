package com.zjj.netdisk.pojo;

import lombok.Builder;
import lombok.Data;
// 引入 JSR 303/380 校验注解，增加接口的健壮性
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * @author 34978
 */
@Data
@Builder
public class UpdatePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6到20位之间")
    private String newPassword;

    private Date lastPasswordUpdateTs;
}