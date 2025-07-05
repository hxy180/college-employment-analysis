package com.shixun.utils;

import lombok.Getter;

@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 通用错误
    FAIL(500, "操作失败"),
    SYSTEM_ERROR(501, "系统异常，请联系管理员"),

    // 参数类
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "未授权或登录失效"),
    FORBIDDEN(403, "无权限访问"),

    // 业务类
    USER_NOT_FOUND(600, "用户不存在"),
    NO_STATISTIC_DATA(602, "暂无统计数据"),
    ORDER_NOT_EXIST(601, "订单不存在");




    private final Integer code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
