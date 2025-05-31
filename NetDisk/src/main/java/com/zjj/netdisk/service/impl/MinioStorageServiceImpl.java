package com.zjj.netdisk.service.impl;

import com.zjj.netdisk.service.MinioStorageService;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MinioStorageServiceImpl implements MinioStorageService {
    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioStorageServiceImpl(MinioClient minioClient, @Value("${minio.bucket}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public void uploadFile(String bucketName, String objectName, String filePath) {
        // TODO: 实现上传逻辑
    }

    @Override
    public void downloadFile(String objectName, String filePath) {
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(filePath)
                            .build());
        } catch (ErrorResponseException e) {
            log.error("MinIO 错误响应: {}", e.errorResponse(), e);
        } catch (InsufficientDataException | InternalException e) {
            log.error("数据传输异常: {}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("IO 异常: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("其他异常: {}", e.toString(), e);
        }
    }
}