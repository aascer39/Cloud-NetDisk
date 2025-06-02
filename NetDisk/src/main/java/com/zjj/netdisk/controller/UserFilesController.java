package com.zjj.netdisk.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.zjj.netdisk.entity.PhysicalFiles;
import com.zjj.netdisk.entity.UserFiles;
import com.zjj.netdisk.exception.FileOperationException;
import com.zjj.netdisk.pojo.FileMetadataDTO;
import com.zjj.netdisk.service.MinioStorageService;
import com.zjj.netdisk.service.PhysicalFilesService;
import com.zjj.netdisk.service.UserFilesService;
import com.zjj.netdisk.utils.FileInfoDetector;
import com.zjj.netdisk.utils.UtilityTools;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Map;

/**
 * @author 34978
 */
@Slf4j
@RestController
@RequestMapping("/userFiles")
public class UserFilesController {
    private final MinioStorageService minioStorageService;
    private final UserFilesService userFilesService;
    private final PhysicalFilesService physicalFilesService;

    @Autowired
    public UserFilesController(MinioStorageService minioStorageService, PhysicalFilesService physicalFilesService, UserFilesService userFilesService) {
        this.minioStorageService = minioStorageService;
        this.userFilesService = userFilesService;
        this.physicalFilesService = physicalFilesService;
    }


    @Operation(summary = "下载文件")
    @RequestMapping("/download")
    public String downloadFile(
            @RequestParam String objectName,
            @RequestParam String parentFolder,
            @RequestParam(required = false) String fileName) {

        // 1. 确保用户已登录
        Long userId = StpUtil.getLoginIdAsLong();
        if (userId == null) {
            log.warn("下载文件尝试失败：用户未登录。");
            return "用户未登录，请先登录";
        }

        // 2. 参数校验：确保基本参数不为空
        if (parentFolder == null || parentFolder.trim().isEmpty()) {
            log.error("下载文件失败: parentFolder 参数不能为空。");
            return "下载文件失败: parentFolder 参数不能为空。";
        }
        if (objectName == null || objectName.trim().isEmpty()) {
            log.error("下载文件失败: objectName 参数不能为空。");
            return "下载文件失败: objectName 参数不能为空。";
        }

        // 3. 清理输入参数，去除可能存在的前后空格
        String trimmedParentFolder = parentFolder.trim();
        String trimmedObjectNameFromRequest = objectName.trim();

        // 7. 构造 MinIO 中实际的对象名称（包含用户ID前缀）
        String minioActualObjectName = userId + "/" + trimmedObjectNameFromRequest;

        try {
            minioStorageService.statObject(objectName);
        } catch (Exception e) {
            log.error("获取文件元数据失败,可能不存在: {}", e.getMessage(), e);
            return "获取文件元数据失败，可能不存在: " + e.getMessage();
        }

        // 4. 确定最终的本地文件名
        String localFileNameComponent;
        if (fileName != null && !fileName.trim().isEmpty()) {
            localFileNameComponent = fileName.trim();
        } else {
            localFileNameComponent = trimmedObjectNameFromRequest;
        }

        // 5. 再次确认 localFileNameComponent 不是空的
        if (localFileNameComponent.isEmpty()) {
            log.error("下载文件失败: 无法确定有效的本地文件名。原始 objectName: '{}', 提供的 fileName: '{}'", objectName, fileName);
            return "下载文件失败: 本地文件名无效。";
        }

        // 6. 构造完整的本地文件路径 (跨平台方式)
        String filePathString; // 将用于传递给服务和日志
        try {
            // 使用 Paths.get() 来安全地拼接路径，它会自动处理操作系统的分隔符
            Path targetPath = Paths.get(trimmedParentFolder, localFileNameComponent);
            filePathString = targetPath.toString();
        } catch (InvalidPathException e) {
            log.error("下载文件失败: 提供的本地父目录 '{}' 或文件名 '{}' 构成无效路径。", trimmedParentFolder, localFileNameComponent, e);
            return "下载文件失败: 本地保存路径或文件名包含无效字符或格式不正确。";
        }


        // 8. 执行下载操作
        try {
            log.info("尝试下载文件: MinIO对象='{}', 保存至本地路径='{}'", minioActualObjectName, filePathString);
            minioStorageService.downloadFile(minioActualObjectName, filePathString);
            return "下载成功，文件已保存到: " + filePathString;
        } catch (Exception e) {
            log.error("下载文件 '{}' 到 '{}' 失败: {}", minioActualObjectName, filePathString, e.getMessage(), e);

            Throwable cause = e.getCause();
            if (e instanceof FileOperationException &&
                    cause instanceof IllegalArgumentException &&
                    cause.getMessage() != null &&
                    cause.getMessage().startsWith("Destination file") &&
                    cause.getMessage().endsWith("already exists")) {
                return "下载文件失败: 指定的本地路径 '" + filePathString + "' 可能是一个已存在的目录，或目标文件已存在且不允许覆盖。";
            }
            return "下载文件失败: " + e.getMessage();
        }
    }

    @Operation(summary = "获取文件元数据")
    @RequestMapping("/getFileMetadata")
    public Object getFileMetadata(String objectName) {
        Long userId = StpUtil.getLoginIdAsLong();
        String trimmedObjectNameFromRequest = objectName.trim();
        objectName = userId + "/" + trimmedObjectNameFromRequest;
        try {
            return FileMetadataDTO.fromStatObjectResponse(minioStorageService.statObject(objectName));
        } catch (Exception e) {
            log.error("获取文件元数据失败: {}", e.getMessage(), e);
            return "获取文件元数据失败: " + e.getMessage();
        }
    }

    @Operation(summary = "上传文件")
    @PostMapping("/upload") // 文件上传通常使用 POST 请求
    public SaResult uploadFile(@RequestParam(value = "objectName", required = false) String objectName, // objectName 可以是可选的，或者从文件名派生
                               @RequestParam("file") MultipartFile file) { // "file" 必须与前端 <a-upload> 组件的 name 属性匹配
        Long userId = StpUtil.getLoginIdAsLong();

        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            return SaResult.error("上传的文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();

        // 2. 确定有效的文件名 (effectiveObjectName)
        // 如果前端传递了 objectName，则使用它；否则，使用原始文件名。
        String effectiveObjectName = (objectName == null || objectName.trim().isEmpty())
                ? originalFilename
                : objectName.trim();

        if (effectiveObjectName == null || effectiveObjectName.trim().isEmpty()) {
            return SaResult.error("文件名不能为空 (无论是自定义的 objectName 还是原始文件名)");
        }

        // 用于存储临时文件
        Path tempFile = null;
        try {
            // 3. 将 MultipartFile 保存为临时文件
            // 这是因为你现有的一些工具方法 (如 getFileSha256ByPath, minioStorageService.uploadFile) 可能需要一个文件路径。
            // 如果这些工具能直接处理 InputStream，则可以避免创建临时文件。
            String prefix = "upload-" + userId + "-";
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            tempFile = Files.createTempFile(prefix, suffix); // 创建带唯一性的临时文件
            try (InputStream inputStream = file.getInputStream()) { // 从 MultipartFile 获取输入流
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING); // 将输入流内容复制到临时文件
            }
            String tempFilePath = tempFile.toAbsolutePath().toString(); // 获取临时文件的绝对路径

            // 4. 构造 MinIO 中实际的对象名称
            // 在文件名后添加时间戳可以确保用户上传同名文件时，在 MinIO 中的对象名是唯一的。
            // 但这也意味着如果仅通过 effectiveObjectName 去检索，可能无法直接匹配（不带时间戳的部分）。
            // 需要考虑这是期望的行为，还是希望覆盖或版本化。
            String minioActualObjectName = userId + "/" + effectiveObjectName + "-" + UtilityTools.getBeijingTimestamp();

            // 5. 计算文件哈希值和获取文件大小
            String fileHash = UtilityTools.getFileSha256ByPath(tempFilePath); // 使用临时文件路径计算哈希
            long fileSize = file.getSize(); // 直接从 MultipartFile 获取文件大小

            PhysicalFiles physicalFiles = physicalFilesService.selectByFileHash(fileHash);

            if (physicalFiles == null) {
                // 如果物理文件记录不存在 (根据哈希值判断)
                // fileSize = FileInfoDetector.getFileSizeInBytes(tempFilePath); // 或者，如果需要通过路径获取，确保此方法有效
                minioStorageService.uploadFile(minioActualObjectName, tempFilePath); // 将临时文件上传到 MinIO

                PhysicalFiles newPhysicalFiles = PhysicalFiles.builder()
                        .fileHash(fileHash)
                        .fileSizeBytes(fileSize)
                        // MinIO 中的存储路径
                        .storagePath(minioActualObjectName)
                        .creationTs(UtilityTools.getBeijingTimestamp())
                        // 新文件引用计数为1
                        .referenceCount(1)
                        // .creationTs(UtilityTools.getBeijingTimestamp()) // 注意：这里似乎重复设置了 creationTs
                        .build();
                physicalFilesService.insertPhysicalFiles(newPhysicalFiles);
                log.info("新文件上传到 MinIO: {}", minioActualObjectName);
            } else {
                // 如果物理文件记录已存在 (文件内容已存在于系统中)
                log.info("文件哈希已存在，物理文件位于: {}. 将引用此物理文件。", physicalFiles.getStoragePath());
                // 使用已存在记录的文件大小
                fileSize = physicalFiles.getFileSizeBytes();

                // 重要: 即便物理文件已存在，用户可能以新的 'effectiveObjectName' "上传"它。
                // 你当前逻辑是为用户的每次上传（即使内容相同）创建一个新的 `minioActualObjectName` (因为加了时间戳)。
                // 这意味着多个 UserFiles 记录可能指向内容相同（哈希相同）但 MinIO 路径不同的对象。
                // 如果去重也意味着复用同一个 MinIO 对象，那么这里的 `minioActualObjectName` 需要调整为 `physicalFiles.getStoragePath()`。
                // 目前假设：即使用户上传了内容相同的文件，只要他指定了不同的 `objectName` 或系统自动生成了不同的时间戳，
                // 就会在 `UserFiles` 中产生一条新记录，并且在 MinIO 中也可能是一个新的对象（如果 `minioActualObjectName` 不同）。
                // 我们需要增加现有 `PhysicalFiles` 的引用计数。
                physicalFiles.setReferenceCount(physicalFiles.getReferenceCount() + 1);
                // 假设你有更新 PhysicalFiles 的方法
                physicalFilesService.updatePhysicalFiles(physicalFiles);
            }

            // 6. 创建 UserFiles 实体 (用户逻辑文件记录)
            UserFiles userFiles = UserFiles.builder()
                    .userId(userId)
                    // 外键，关联到 PhysicalFiles 的哈希
                    .fileHashFk(fileHash)
                    .fileSizeBytes(fileSize)
                    // 或者一个更动态的父文件夹路径
                    .parentFolder(userId + "/")
                    // 用户看到的文件名
                    .itemName(effectiveObjectName)
                    .itemType("file")
                    // 从 MultipartFile 获取 MIME 类型
                    .mimeType(file.getContentType())
                    // .mimeType(FileInfoDetector.getContentTypeUsingNio(tempFilePath)) // 或者使用你的检测器，如果更可靠
                    .creationTs(UtilityTools.getBeijingTimestamp())
                    .modificationTs(UtilityTools.getBeijingTimestamp())
                    .status("active")
                    .build();
            userFilesService.insertUserFiles(userFiles);
            // 返回成功信息，可以附带一些数据给前端
            return SaResult.ok("文件上传成功");

        } catch (IOException e) {
            log.error("文件处理失败 (IO异常): {}", e.getMessage(), e);
            return SaResult.error("文件处理失败，请稍后再试。");
        } catch (DataIntegrityViolationException e) { // 例如：捕获 UserFiles 表的唯一约束冲突
            log.error("文件名可能重复 (数据库约束冲突): userId={}, parentFolder={}, itemName={}", userId, userId + "/", effectiveObjectName, e);
            return SaResult.error("文件名 '" + effectiveObjectName + "' 已存在于当前位置，请修改您的文件名。");
        } catch (Exception e) {
            log.error("上传文件失败 (未知错误): {}", e.getMessage(), e);
            // 避免直接暴露数据库约束信息给用户，除非是特意捕获并处理的。
            return SaResult.error("文件上传失败，请检查文件或联系管理员。");
        } finally {
            // 7. 清理临时文件
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.error("无法删除临时文件: {}", tempFile.toString(), e);
                }
            }
        }
    }

}
