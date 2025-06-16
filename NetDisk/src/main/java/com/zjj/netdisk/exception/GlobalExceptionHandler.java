package com.zjj.netdisk.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.zjj.netdisk.pojo.response.GlobalResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author 34978
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获并处理“未登录”异常
     * 当用户访问需要登录的接口但未登录时，Sa-Token会抛出此异常
     */
    @ExceptionHandler(NotLoginException.class)
    public GlobalResponse<?> handleNotLoginException(NotLoginException e) {
        // 打印堆栈，方便开发阶段调试
        e.printStackTrace();

        // 根据异常类型判断具体原因，返回友好提示
        String message;
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未能读取到有效Token，请先登录";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "Token无效，请重新登录";
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "Token已过期，请重新登录";
        } else {
            message = "未登录，请先登录";
        }

        // 返回给前端的JSON
        return GlobalResponse.error(401, message);
    }

    /**
     * 捕获并处理“没有权限”异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public GlobalResponse<?> handleNotPermissionException(NotPermissionException e) {
        e.printStackTrace();
        return GlobalResponse.error(403, "您没有访问该功能的权限：" + e.getPermission());
    }

    /**
     * 捕获并处理“没有角色”异常
     */
    @ExceptionHandler(NotRoleException.class)
    public GlobalResponse<?> handleNotRoleException(NotRoleException e) {
        e.printStackTrace();
        return GlobalResponse.error(403, "您没有访问该功能的权限，需要角色：" + e.getRole());
    }

    /**
     * 捕获并处理其他所有未知异常
     */
    @ExceptionHandler(Exception.class)
    public GlobalResponse<?> handleException(Exception e) {
        e.printStackTrace();
        return GlobalResponse.error(500, "服务器发生未知错误，请联系管理员");
    }
}