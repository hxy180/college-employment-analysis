package com.shixun.utils;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode code;
    private final String message;

    public BusinessException(ErrorCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode;
        this.message = null;
    }
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage());
        this.code = errorCode;
        this.message = message;
    }
    public BusinessException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }




}
