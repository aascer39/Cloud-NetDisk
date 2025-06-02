package com.zjj.netdisk.service;

import io.minio.StatObjectResponse;

/**
 * @author 34978
 */
public interface MinioStorageService {
    void uploadFile(String objectName, String filePath);
    void downloadFile(String objectName, String filePath);
    StatObjectResponse statObject(String objectName) throws Exception;
    void createDirectory(Long userId) throws Exception;
}
