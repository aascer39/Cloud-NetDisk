package com.zjj.netdisk.service;

/**
 * @author 34978
 */
public interface MinioStorageService {
    void uploadFile(String bucketName, String objectName, String filePath);
    void downloadFile(String objectName, String filePath);
}
