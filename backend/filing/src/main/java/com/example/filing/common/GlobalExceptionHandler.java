package com.example.filing.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常（RuntimeException）→ 统一返回 500 + message */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntime(RuntimeException e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }

    /** 其他异常 → 统一返回 500 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.fail("服务器内部错误: " + e.getMessage());
    }
}