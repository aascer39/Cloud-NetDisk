package com.zjj.netdisk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 34978
 */
@SpringBootApplication
@MapperScan("com.zjj.netdisk.mapper")
public class NetDiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetDiskApplication.class, args);
    }

}
