package com.zjj.netdisk.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author 34978
 */
@Data
@Builder
public class LoginDTO {
    private String username;

    private String password;
}
