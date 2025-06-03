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
import java.util.List;
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
    @PostMapping("/upload")
    public SaResult uploadFile(List<MultipartFile> multipartFiles) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (userId == null) {
            log.warn("用户未登录，无法上传文件。");
            return SaResult.error("用户未登录，无法上传文件。");
        }

        minioStorageService.uploadFiles(multipartFiles, userId);
        return SaResult.ok("上传成功");
    }

}
