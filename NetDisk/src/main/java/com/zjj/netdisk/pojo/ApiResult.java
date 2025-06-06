package com.zjj.netdisk.pojo;

import lombok.Data;

/**
 * @author 34978
 */
@Data
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    // 私有化构造函数，强制使用静态工厂方法
    private ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // --- 成功的静态方法 ---
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    public static ApiResult<Void> success(String message) {
        return new ApiResult<>(200, message, null);
    }


    // --- 失败的静态方法 ---
    public static ApiResult<Void> error(int code, String message) {
        return new ApiResult<>(code, message, null);
    }

    // 默认错误码为500
    public static ApiResult<Void> error(String message) {
        return new ApiResult<>(500, message, null);
    }
}