package com.zjj.netdisk.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 34978
 */
@Data
@Builder
@NoArgsConstructor
public class GlobalResponse<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    // 私有化构造函数，强制使用静态工厂方法
    private GlobalResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // --- 成功的静态方法 ---
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(200, "操作成功", data);
    }

    public static <T> GlobalResponse<T> success(String message, T data) {
        return new GlobalResponse<>(200, message, data);
    }

    public static GlobalResponse<Void> success(String message) {
        return new GlobalResponse<>(200, message, null);
    }


    // --- 失败的静态方法 ---
    public static GlobalResponse<Void> error(int code, String message) {
        return new GlobalResponse<>(code, message, null);
    }

    // 默认错误码为500
    public static GlobalResponse<Void> error(String message) {
        return new GlobalResponse<>(500, message, null);
    }
}