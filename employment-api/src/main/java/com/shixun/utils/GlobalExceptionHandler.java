package com.shixun.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    // 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex) {

        ErrorCode errorCode = ex.getCode();
        String message = ex.getMessage(); // 优先取异常中自定义的消息

        // 如果异常没有自定义消息（即使用的是 BusinessException(ErrorCode code) 构造），则使用 ErrorCode 的默认消息
        if (message == null || message.isEmpty()) {
            message = errorCode.getMessage();
        }
        // 记录异常日志（便于排查问题）
        log.warn("业务异常：code={}, message={}", errorCode.getCode(), message, ex);
        // 返回包含错误码和具体消息的响应
        return Result.error(errorCode.getCode(), message);
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        ex.printStackTrace();
        return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "服务器异常：" + ex.getMessage());
    }
}
