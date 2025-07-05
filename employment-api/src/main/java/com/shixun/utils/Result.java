package com.shixun.utils;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMessage());
        r.setData(data);
        return r;
    }

    public static Result<?> error(ResultCode resultCode) {
        Result<?> r = new Result<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMessage());
        return r;
    }

    public static Result<?> error(ResultCode resultCode, String customMessage) {
        Result<?> r = new Result<>();
        r.setCode(resultCode.getCode());
        r.setMsg(customMessage);
        return r;
    }

    public static Result<?> error(int code, String msg) {
        Result<?> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static Result<?> error(ErrorCode code) {
        Result<?> r = new Result<>();
        r.setCode(code.getCode());
        r.setMsg(code.getMessage());
        return r;
    }
}
