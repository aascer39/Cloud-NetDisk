package com.zjj.netdisk.pojo;

import lombok.Data;

/**
 * @author 34978
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private boolean needHashed;
}