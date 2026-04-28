package com.ai.common;

import lombok.Data;

/**
 * 统一 API 返回结果
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    // 私有构造，禁止外部直接 new
    private Result() {}

    // 成功返回（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResponseCode.SUCCESS.getCode());
        result.setMessage(ResponseCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    // 成功返回（无数据）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResponseCode.SUCCESS.getCode());
        result.setMessage(ResponseCode.SUCCESS.getMessage());
        return result;
    }

    // 失败返回（枚举类型）
    public static <T> Result<T> fail(ResponseCode responseCode) {
        Result<T> result = new Result<>();
        result.setCode(responseCode.getCode());
        result.setMessage(responseCode.getMessage());
        return result;
    }

    // 接收 ResponseCode 枚举的 error 方法（可选，更优雅）
    public static <T> Result<T> error(ResponseCode code) {
        return fail(code);
    }
}