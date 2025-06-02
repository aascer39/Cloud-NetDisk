package com.zjj.netdisk.service.impl;

import com.zjj.netdisk.exception.FileOperationException;
import com.zjj.netdisk.service.MinioStorageService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author 34978
 */
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

    public void uploadFile(String objectName, String localFilePath) throws FileOperationException {
        if (localFilePath == null || localFilePath.trim().isEmpty()) {
            throw new FileOperationException("本地文件路径不能为空。");
        }
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new FileOperationException("MinIO中的对象名称不能为空。");
        }

        File file = new File(localFilePath);
        if (!file.exists()) {
            // 日志中的错误信息是 "文件名、目录名或卷标语法不正确。"
            // 这通常意味着文件找不到，或者路径本身有问题。
            // 使用 file.exists() 可以更明确地判断。
            String specificError = " (系统找不到指定的文件或路径语法不正确)";
            log.error("本地文件未找到或路径无效: {} {}", localFilePath, specificError);
            throw new FileOperationException("本地源文件未找到或路径无效: " + localFilePath + specificError,
                    new FileNotFoundException(localFilePath + specificError));
        }
        if (!file.isFile()) {
            log.error("本地路径不是一个文件: {}", localFilePath);
            throw new FileOperationException("指定的本地路径不是一个文件: " + localFilePath);
        }
        if (!file.canRead()) {
            log.error("本地文件不可读: {}", localFilePath);
            throw new FileOperationException("没有权限读取本地文件: " + localFilePath);
        }

        // 默认MIME类型
        String contentType = "application/octet-stream";
        try {
            Path path = Paths.get(localFilePath);
            String detectedContentType = Files.probeContentType(path);
            if (detectedContentType != null) {
                contentType = detectedContentType;
            } else {
                log.warn("无法自动检测文件 {} 的MIME类型，将使用默认值: {}", localFilePath, contentType);
            }
        } catch (IOException e) {
            log.warn("检测文件 {} 的MIME类型时发生IO错误，将使用默认值: {} (错误: {})", localFilePath, contentType, e.getMessage());
        }

        // 使用 try-with-resources确保输入流被关闭
        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            // 已知文件大小，所以objectSize用file.length()，partSize用-1让SDK自动处理
//                            .stream(inputStream, file.length(), -1)
                            // 或者，如果您仍想使用未知大小流式传输（尽管源是文件）：
                            // L 表示 long 类型
                            .stream(inputStream, -1, 10485760L)
                            .contentType(contentType)
                            .build());
            log.info("文件 {} 成功上传到 MinIO 存储桶 {}，对象名为 {}", localFilePath, bucketName, objectName);
        } catch (FileNotFoundException e) { // 这个理论上不应该在这里被再次捕获，因为上面已经检查了
            log.error("文件未找到 (try-with-resources block): {} - {}", localFilePath, e.getMessage(), e);
            throw new FileOperationException("上传失败：源文件未找到 " + localFilePath, e);
        } catch (IOException e) { // 其他IO错误，例如读取流时发生问题
            log.error("上传文件 {} 到对象 {} 时发生IO错误: {}", localFilePath, objectName, e.getMessage(), e);
            throw new FileOperationException("上传文件时发生I/O错误: " + e.getMessage(), e);
        } catch (ErrorResponseException e) { // MinIO返回的错误响应
            log.error("MinIO错误响应，上传对象 {}: Code='{}', Message='{}', Bucket='{}'", objectName, e.errorResponse().code(), e.errorResponse().message(), e.errorResponse().bucketName(), e);
            throw new FileOperationException("MinIO操作失败: " + e.errorResponse().message(), e);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 NoSuchAlgorithmException | ServerException | XmlParserException e) { // 其他MinIO SDK特定的受检异常
            log.error("MinIO SDK特定异常，上传对象 {}: {}", objectName, e.getMessage(), e);
            throw new FileOperationException("MinIO SDK操作时发生错误: " + e.getMessage(), e);
        } catch (RuntimeException e) { // 捕获其他未预料到的运行时异常
            log.error("上传文件时发生意外的运行时错误，对象 {}: {}", objectName, e.getMessage(), e);
            throw new FileOperationException("上传时发生意外错误: " + e.getMessage(), e);
        }
    }

    @Override
    public void downloadFile(String objectName, String filePath) {
        // Logger log = LoggerFactory.getLogger(YourClassName.class); // 获取Logger实例
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(filePath)
                            .build());
        } catch (ErrorResponseException e) {
            String errMsg = String.format("MinIO 错误响应 (对象: %s): %s", objectName, e.errorResponse() != null ? e.errorResponse().message() : e.getMessage());
            // 向上抛出自定义运行时异常
            throw new FileOperationException(errMsg, e);
        } catch (InsufficientDataException e) {
            String errMsg = String.format("MinIO 数据不足 (对象: %s): %s", objectName, e.getMessage());
            // 向上抛出自定义运行时异常
            throw new FileOperationException(errMsg, e);
        } catch (InternalException e) { // MinIO 内部服务器错误或网络错误
            String errMsg = String.format("MinIO 内部错误 (对象: %s): %s", objectName, e.getMessage());
            // 向上抛出自定义运行时异常
            throw new FileOperationException(errMsg, e);
        } catch (IOException e) { // 通常是文件系统相关的IO错误，比如写入filePath失败
            String errMsg = String.format("文件IO错误 (对象: %s, 文件路径: %s): %s", objectName, filePath, e.getMessage());
            // 向上抛出自定义运行时异常
            throw new FileOperationException(errMsg, e);
        } catch (InvalidKeyException | NoSuchAlgorithmException | XmlParserException | InvalidResponseException |
                 ServerException | IllegalArgumentException e) {
            // 其他特定的MinIO SDK异常或参数错误
            // IllegalArgumentException 可能是由于 builder 参数问题导致
            String errMsg = String.format("MinIO 操作或参数错误 (对象: %s): %s", objectName, e.getMessage());
            // 向上抛出自定义运行时异常
            throw new FileOperationException(errMsg, e);
        } catch (Exception e) {
            // 捕获所有其他未预料到的异常
            String errMsg = String.format("下载文件时发生未知错误 (对象: %s): %s", objectName, e.toString());
            // 避免重复包装 FileOperationException
            if (e instanceof FileOperationException) {
                throw (FileOperationException) e;
            }
            // 向上抛出自定义运行时异常
            throw new FileOperationException(errMsg, e);
        }
    }

    @Override
    public StatObjectResponse statObject(String objectName) throws Exception {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return stat;
        } catch (ErrorResponseException e) {
            log.error("MinIO 错误响应: {}", e.errorResponse(), e);
            throw e;
        } catch (InsufficientDataException | InternalException e) {
            log.error("数据传输异常: {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error("IO 异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void createDirectory(Long userId) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        String directoryName = userId + "/";
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(directoryName)
                            .stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
                            .build());
            log.info("目录 {} 成功创建在 MinIO 存储桶 {}", directoryName, bucketName);
        } catch (ErrorResponseException e) {
            log.error("MinIO 错误响应: {}", e.errorResponse(), e);
            throw e;
        } catch (InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("创建目录时发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }
}