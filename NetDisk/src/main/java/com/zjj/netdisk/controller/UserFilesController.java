package com.zjj.netdisk.controller;

import com.zjj.netdisk.service.MinioStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 34978
 */
@Slf4j
@RestController
@RequestMapping("/userFiles")
public class UserFilesController {
    private final MinioStorageService minioStorageService;

    @Autowired
    public UserFilesController(MinioStorageService minioStorageService) {
        this.minioStorageService = minioStorageService;
    }

    @Operation(summary = "下载文件")
    @RequestMapping("/download")
    public String downloadFile(String objectName, String filePath) {
        minioStorageService.downloadFile(objectName, filePath);
        // 假设下载成功后重定向到成功页面
        return "下载成功，文件已保存到: " + filePath;
    }
}
