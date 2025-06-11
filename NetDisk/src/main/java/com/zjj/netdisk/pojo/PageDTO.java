package com.zjj.netdisk.pojo;

import lombok.Data;

/**
 * @author 34978
 */
@Data
public class PageDTO {
    private long current = 1;
    private long size = 10;
    
    // 查询参数
    // 按名称模糊查询
    private String nameKeyword;
    // 按状态精确查询
    private Integer status;
    // 按邮箱后缀查询
    private String emailDomain;
}