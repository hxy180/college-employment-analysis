package com.shixun.utils;

import lombok.Getter;

/**
 * 错误码枚举：定义系统中所有可能的错误类型
 */
@Getter
public enum ErrorCode {

    // 通用错误
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统异常"),
    INVALID_PARAM(400, "参数不合法"),

    // 业务错误（专业数据相关）
    MAJOR_NOT_FOUND(1001, "专业数据不存在"),
    DATA_EMPTY(1001, "数据不存在"),
    STAT_DATE_EMPTY(1002, "统计日期不能为空"),
    DATA_QUERY_FAILED(1003, "数据查询失败"),
    DATA_NOT_EMPTY(1003, "参数为空"),
    UNKNOWN_ERROR(404, "系统异常，请稍后重试");

    /** 错误码 */
    private final int code;

    /** 错误消息 */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}