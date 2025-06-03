package com.zjj.netdisk.service;

import io.minio.StatObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 34978
 */
public interface MinioStorageService {
    void uploadFiles(List<MultipartFile> files, long userId);

    void downloadFile(String objectName, String filePath);
    StatObjectResponse statObject(String objectName) throws Exception;
    void createDirectory(Long userId) throws Exception;
}
