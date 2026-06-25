package com.oa.system.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e) {
        return R.fail("系统繁忙，请稍后重试");
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        e.printStackTrace();
        return R.fail("系统错误，请联系管理员");
    }
}
