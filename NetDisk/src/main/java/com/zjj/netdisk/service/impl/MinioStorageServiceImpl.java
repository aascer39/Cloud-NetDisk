package com.zjj.netdisk.service.impl;

import com.zjj.netdisk.entity.DTO.PhysicalFilesDTO;
import com.zjj.netdisk.pojo.UpdateUserDTO;
import com.zjj.netdisk.entity.DTO.UserFilesDTO;
import com.zjj.netdisk.entity.DTO.UsersDTO;
import com.zjj.netdisk.exception.FileOperationException;
import com.zjj.netdisk.service.MinioStorageService;
import com.zjj.netdisk.service.PhysicalFilesService;
import com.zjj.netdisk.service.UserFilesService;
import com.zjj.netdisk.service.UsersService;
import com.zjj.netdisk.utils.UtilityTools;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 34978
 */
@Service
@Slf4j
public class MinioStorageServiceImpl implements MinioStorageService {
    private final MinioClient minioClient;
    private final String bucketName;
    private final PhysicalFilesService physicalFilesService;
    private final UserFilesService userFilesService;
    private final UsersService usersService;

    @Autowired
    public MinioStorageServiceImpl(MinioClient minioClient, @Value("${minio.bucket}") String bucketName, PhysicalFilesService physicalFilesService, UserFilesService userFilesService, UsersService usersService) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.physicalFilesService = physicalFilesService;
        this.userFilesService = userFilesService;
        this.usersService = usersService;
    }

    @Override
    public void uploadFiles(List<MultipartFile> files, long userId) {
        if (files == null || files.isEmpty()) {
            log.info("没有文件需要上传 (userId: {})。", userId);
            return;
        }

        UsersDTO user = usersService.getUserById(userId);
        long usedStorage = user.getUsedStorageBytes();
        for (MultipartFile file : files) {
            usedStorage += file.getSize();
        }
        if (usedStorage > user.getStorageQuotaBytes()) {
            log.warn("用户 {} 的存储空间已满，无法上传文件。", userId);
            throw new FileOperationException("用户存储空间不足，无法上传文件");
        } else {
            // 更新用户的已用存储空间
            UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                    .usedStorageBytes(usedStorage)
                    .build();
            usersService.updateUser(userId, updateUserDTO);
            log.info("用户 {} 的存储空间已更新，当前已用空间: {} 字节。", userId, usedStorage);
        }

        // SnowballObject 列表
        ArrayList<SnowballObject> objects = new ArrayList<>(files.size());
        // 需要关闭的 FileInputStream
        List<InputStream> streamsToClose = new ArrayList<>();
        // 需要删除的临时文件
        List<Path> tempFilesToDelete = new ArrayList<>();

        for (MultipartFile file : files) {
            // 100MB，超过此大小将使用临时文件处理
            long maxSizeForInMemoryProcessing = 100 * 1024 * 1024;
            if (file.isEmpty()) {
                log.warn("跳过一个空文件 (userId: {})。", userId);
                continue;
            }

            String effectiveObjectName = file.getOriginalFilename();
            if (effectiveObjectName == null || effectiveObjectName.isBlank()) {
                effectiveObjectName = "untitled-" + UtilityTools.getBeijingTimestamp();
            }

            // 用户上传的原始文件大小
            long originalFileSize = file.getSize();
            String mimeType = file.getContentType();
            String fileHash = null;

            try {
                // 1. 计算文件哈希值
                try (InputStream inputStreamForHash = file.getInputStream()) {
                    fileHash = UtilityTools.getFileHash(inputStreamForHash);
                } catch (IOException | NoSuchAlgorithmException e) {
                    log.error("计算文件哈希失败: 文件名='{}', 用户ID='{}', 错误: {}", effectiveObjectName, userId, e.getMessage(), e);
                    continue;
                }

                PhysicalFilesDTO physicalFile = physicalFilesService.selectByFileHash(fileHash);
                // 用于 PhysicalFilesDTO 和 SnowballObject 的大小
                long sizeForDbAndSnowball = originalFileSize;

                if (physicalFile == null) {
                    // 2a. 物理文件不存在 - 准备 SnowballObject 并创建 PhysicalFilesDTO 记录
                    String physicalObjectStorageName = userId + "/" + UtilityTools.getBeijingTimestamp() + "_" + effectiveObjectName;
                    InputStream streamForSnowballObject = null;

                    if (originalFileSize <= maxSizeForInMemoryProcessing) {
                        // --- 小文件：加载到内存 ---
                        byte[] fileData;
                        try (InputStream initialStream = file.getInputStream()) {
                            fileData = initialStream.readAllBytes();
                            // 使用内存中数据的实际大小
                            sizeForDbAndSnowball = fileData.length;
                        } catch (IOException e) {
                            log.error("小文件 '{}' (用户ID: '{}'): 读取内容到内存失败: {}",
                                    effectiveObjectName, userId, e.getMessage(), e);
                            continue;
                        }
                        streamForSnowballObject = new ByteArrayInputStream(fileData);
                        log.info("小文件 '{}' (用户ID: '{}'): 已准备 SnowballObject (内存缓冲), 大小: {}, MinIO名: '{}'",
                                effectiveObjectName, userId, sizeForDbAndSnowball, physicalObjectStorageName);
                        // ByteArrayInputStream 不需要添加到 streamsToClose，它没有底层系统资源需要释放
                    } else {
                        // --- 大文件：使用临时文件 ---
                        Path tempFilePath = null;
                        try {
                            tempFilePath = Files.createTempFile("upload-large-" + userId + "-", "-" + effectiveObjectName);
                            try (InputStream initialStream = file.getInputStream()) {
                                Files.copy(initialStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
                            }
                            // 使用临时文件的实际大小
                            sizeForDbAndSnowball = Files.size(tempFilePath);

                            FileInputStream fileInputStream = new FileInputStream(tempFilePath.toFile());
                            streamForSnowballObject = fileInputStream;
                            // 添加到待关闭列表
                            streamsToClose.add(fileInputStream);
                            // 添加到待删除列表
                            tempFilesToDelete.add(tempFilePath);

                            log.info("大文件 '{}' (用户ID: '{}'): 已准备 SnowballObject (临时文件: {}), 大小: {}, MinIO名: '{}'",
                                    effectiveObjectName, userId, tempFilePath, sizeForDbAndSnowball, physicalObjectStorageName);
                        } catch (IOException e) {
                            log.error("大文件 '{}' (用户ID: '{}'): 处理失败 (临时文件: {}), 错误: {}",
                                    effectiveObjectName, userId, tempFilePath, e.getMessage(), e);
                            if (tempFilePath != null) {
                                try {
                                    Files.deleteIfExists(tempFilePath);
                                } catch (IOException ex) {
                                    log.warn("尝试删除失败的临时文件 {} 出错: {}", tempFilePath, ex.getMessage());
                                }
                            }
                            continue;
                        }
                    }

                    objects.add(
                            new SnowballObject(
                                    physicalObjectStorageName,
                                    streamForSnowballObject,
                                    sizeForDbAndSnowball,
                                    null
                            )
                    );

                    PhysicalFilesDTO newPhysicalFile = PhysicalFilesDTO.builder()
                            .fileHash(fileHash)
                            // 存储实际准备的数据大小
                            .fileSizeBytes(sizeForDbAndSnowball)
                            .storagePath(physicalObjectStorageName)
                            .referenceCount(1)
                            .creationTs(UtilityTools.getBeijingTimestamp())
                            .build();
                    physicalFilesService.insertPhysicalFiles(newPhysicalFile);
                } else {
                    // 文件内容已存在
                    log.info("文件内容已存在 (哈希: {}), 物理路径: '{}'. 用户ID: '{}', 文件名: '{}'. 引用计数将增加。",
                            fileHash, physicalFile.getStoragePath(), userId, effectiveObjectName);
                    physicalFile.setReferenceCount(physicalFile.getReferenceCount() + 1);
                    physicalFilesService.updatePhysicalFiles(physicalFile);
                }

                // 3. 创建 UserFilesDTO 记录 (用户逻辑文件记录)
                String userLogicalParentFolder = userId + "/";
                UserFilesDTO userFile = UserFilesDTO.builder()
                        .userId(userId)
                        .fileHashFk(fileHash)
                        .fileSizeBytes(originalFileSize)
                        .parentFolder(userLogicalParentFolder)
                        .itemName(effectiveObjectName)
                        .itemType("file")
                        .mimeType(mimeType)
                        .creationTs(UtilityTools.getBeijingTimestamp())
                        .modificationTs(UtilityTools.getBeijingTimestamp())
                        .status("active")
                        .build();
                try {
                    userFilesService.insertUserFiles(userFile);
                    log.info("UserFilesDTO 记录已创建: 用户ID='{}', 逻辑路径='{}{}'",
                            userId, userLogicalParentFolder, effectiveObjectName);
                } catch (DataIntegrityViolationException e) {
                    log.error("创建 UserFilesDTO 记录失败 (可能文件名冲突): 用户ID='{}', 逻辑路径='{}{}'. 哈希='{}'. 错误: {}",
                            userId, userLogicalParentFolder, effectiveObjectName, fileHash, e.getMessage(), e);
                    // ... (补偿逻辑注释)
                }

            } catch (Exception e) {
                log.error("处理文件 '{}' (用户ID: '{}') 时发生未知错误: {}", effectiveObjectName, userId, e.getMessage(), e);
            }
        } // for 循环结束

        // 4. 在所有文件处理完毕后，统一进行 Snowball 对象的批量上传
        if (!objects.isEmpty()) {
            try {
                log.info("准备批量上传 {} 个新物理文件到 MinIO via Snowball (用户ID: {})。", objects.size(), userId);

                // 假设 uploadSnowballObjects 是同步的，直接返回 ObjectWriteResponse
                ObjectWriteResponse response = minioClient.uploadSnowballObjects(
                        UploadSnowballObjectsArgs.builder()
                                .bucket(bucketName)
                                .objects(objects)
                                .build());

                log.info("批量上传 Snowball Objects 成功 (用户ID: {}). Response ETag: {}", userId, response.etag());

            } catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                log.error("批量上传 Snowball Objects 到 MinIO 失败: 用户ID='{}', 错误: {}", userId, cause.getMessage(), cause);
                if (cause != null && cause != e) {
                    log.error("根本原因 (Caused by): ", cause);
                }
                throw new FileOperationException("批量上传 Snowball Objects 到 MinIO 失败", cause);
            } finally {
                // 清理资源：关闭所有 FileInputStream 并删除所有临时文件
                log.info("开始关闭 {} 个上传流并删除 {} 个临时文件...", streamsToClose.size(), tempFilesToDelete.size());
                for (InputStream stream : streamsToClose) {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException eClose) {
                            log.warn("关闭上传文件流失败: {}", eClose.getMessage(), eClose);
                        }
                    }
                }
                streamsToClose.clear();

                for (Path tempFile : tempFilesToDelete) {
                    if (tempFile != null) {
                        try {
                            Files.deleteIfExists(tempFile);
                            log.debug("已删除临时文件: {}", tempFile);
                        } catch (IOException eDelete) {
                            log.warn("删除临时文件 {} 失败: {}", tempFile, eDelete.getMessage(), eDelete);
                        }
                    }
                }
                tempFilesToDelete.clear();
            }
        } else {
            log.info("没有新的物理文件需要通过 Snowball 上传 (用户ID: {})。", userId);
        }

        log.info("文件批量上传处理流程完成 (用户ID: {})。", userId);
    }

    //    @Override
//    public void downloadFile(String objectName, long userId) {
//        try {
//            minioClient.downloadObject(
//                    DownloadObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(objectName)
//                            .filename()
//                            .build());
//        } catch (ErrorResponseException e) {
//            String errMsg = String.format("MinIO 错误响应 (对象: %s): %s", objectName, e.errorResponse() != null ? e.errorResponse().message() : e.getMessage());
//            // 向上抛出自定义运行时异常
//            throw new FileOperationException(errMsg, e);
//        } catch (InsufficientDataException e) {
//            String errMsg = String.format("MinIO 数据不足 (对象: %s): %s", objectName, e.getMessage());
//            // 向上抛出自定义运行时异常
//            throw new FileOperationException(errMsg, e);
//        } catch (InternalException e) { // MinIO 内部服务器错误或网络错误
//            String errMsg = String.format("MinIO 内部错误 (对象: %s): %s", objectName, e.getMessage());
//            // 向上抛出自定义运行时异常
//            throw new FileOperationException(errMsg, e);
//        } catch (IOException e) { // 通常是文件系统相关的IO错误，比如写入filePath失败
//            String errMsg = String.format("文件IO错误 (对象: %s, 文件路径: %s): %s", objectName, filePath, e.getMessage());
//            // 向上抛出自定义运行时异常
//            throw new FileOperationException(errMsg, e);
//        } catch (InvalidKeyException | NoSuchAlgorithmException | XmlParserException | InvalidResponseException |
//                 ServerException | IllegalArgumentException e) {
//            // 其他特定的MinIO SDK异常或参数错误
//            // IllegalArgumentException 可能是由于 builder 参数问题导致
//            String errMsg = String.format("MinIO 操作或参数错误 (对象: %s): %s", objectName, e.getMessage());
//            // 向上抛出自定义运行时异常
//            throw new FileOperationException(errMsg, e);
//        } catch (Exception e) {
//            // 捕获所有其他未预料到的异常
//            String errMsg = String.format("下载文件时发生未知错误 (对象: %s): %s", objectName, e.toString());
//            // 避免重复包装 FileOperationException
//            if (e instanceof FileOperationException) {
//                throw (FileOperationException) e;
//            }
//            // 向上抛出自定义运行时异常
//            throw new FileOperationException(errMsg, e);
//        }
//    }
//
    @Override
    public StatObjectResponse statObject(String objectName) throws Exception {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
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

}