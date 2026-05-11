package com.oa.system.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e) {
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        e.printStackTrace();
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        error.put("type", e.getClass().getSimpleName());
        return R.fail("系统错误: " + e.getMessage());
    }
}
