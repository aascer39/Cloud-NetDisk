package com.zjj.netdisk.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInfoDetector {

    /**
     * 使用 Files.probeContentType 获取文件的 Content-Type。
     * 这是推荐的现代方法。
     *
     * @param filePath 文件的路径字符串
     * @return 文件的 Content-Type，如果无法确定则返回 null
     */
    public static String getContentTypeUsingNio(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("文件路径不能为空。");
            return null;
        }
        Path path = Paths.get(filePath);
        try {
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                // 如果 Files.probeContentType 无法确定，可以尝试基于文件扩展名的后备方法
                return getContentTypeByExtension(filePath);
            }
            return contentType;
        } catch (IOException e) {
            System.err.println("无法探测文件 '" + filePath + "' 的 Content-Type: " + e.getMessage());
            // 发生IO异常时，也可以尝试基于扩展名
            return getContentTypeByExtension(filePath);
        }
    }

    /**
     * 一个非常基础的基于文件扩展名猜测 Content-Type 的后备方法。
     *
     * @param filePath 文件路径
     * @return 推测的 Content-Type，未知则返回 "application/octet-stream"
     */
    public static String getContentTypeByExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            // 默认类型
            return "application/octet-stream";
        }
        String fileName = Paths.get(filePath).getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            String extension = fileName.substring(lastDot + 1).toLowerCase();
            switch (extension) {
                case "txt":
                    return "text/plain";
                case "html":
                case "htm":
                    return "text/html";
                case "css":
                    return "text/css";
                case "js":
                    return "application/javascript";
                case "json":
                    return "application/json";
                case "xml":
                    return "application/xml";
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "bmp":
                    return "image/bmp";
                case "svg":
                    return "image/svg+xml";
                case "pdf":
                    return "application/pdf";
                case "doc":
                    return "application/msword";
                case "docx":
                    return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                case "xls":
                    return "application/vnd.ms-excel";
                case "xlsx":
                    return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                case "ppt":
                    return "application/vnd.ms-powerpoint";
                case "pptx":
                    return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
                case "zip":
                    return "application/zip";
                case "gz":
                    return "application/gzip";
                case "tar":
                    return "application/x-tar";
                case "mp3":
                    return "audio/mpeg";
                case "wav":
                    return "audio/wav";
                case "mp4":
                    return "video/mp4";
                case "webm":
                    return "video/webm";
                case "ogg":
                    return "application/ogg";
                // 可以根据需要添加更多类型
                default:
                    // 未知或通用二进制类型
                    return "application/octet-stream";
            }
        }
        // 没有扩展名或无法识别
        return "application/octet-stream";
    }

    /**
     * 获取指定文件的文件大小（以字节为单位）。
     * 使用 java.nio.file.Files.size() 方法。
     *
     * @param filePath 文件的路径字符串
     * @return 文件的大小（字节数），如果文件不存在、不是常规文件或发生IO错误，则返回 -1L。
     */
    public static long getFileSizeInBytes(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("文件路径不能为空。");
            return -1L; // 表示错误或无效输入
        }
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.err.println("文件不存在: " + filePath);
            return -1L; // 表示文件未找到
        }
        if (!Files.isRegularFile(path)) {
            System.err.println("提供的路径不是一个常规文件: " + filePath);
            return -1L; // 通常只对常规文件获取大小
        }
        try {
            return Files.size(path);
        } catch (IOException e) {
            System.err.println("无法获取文件 '" + filePath + "' 的大小: " + e.getMessage());
            return -1L; // 表示发生IO错误
        }
    }

}