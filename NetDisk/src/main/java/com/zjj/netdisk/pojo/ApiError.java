package com.zjj.netdisk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 34978
 */
@Data
@Builder
public class ApiError {

    /**
     * 错误发生时间
     * ISO 8601 格式，例如：2025-06-01T10:15:30.123+08:00
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime timestamp;

    /**
     * HTTP 状态码 (例如 400, 404, 500)
     */
    private int status;

    /**
     * 错误类型的简短描述 (例如 "Not Found", "Bad Request")
     */
    private String error;

    /**
     * 给用户看的、更详细的错误信息
     */
    private String message;

    /**
     * 发生错误的请求路径
     */
    private String path;

    /**
     * 应用内部定义的错误码 (可选)
     */
    private String errorCode;

    /**
     * 更具体的错误详情，例如验证错误列表 (可选)
     */
    private Object details;
}