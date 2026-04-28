package com.ai.common;

import lombok.Getter;

/**
 * 统一响应码枚举
 */
@Getter
public enum ResponseCode {

    // 成功
    SUCCESS(200, "操作成功"),
    // 参数错误
    PARAM_ERROR(400, "参数错误"),
    // 失败
    FAIL(401, "操作失败,请重试"),
    // 无权限
    FORBIDDEN(402, "无权限访问"),
    // 资源不存在
    NOT_FOUND(403, "资源不存在"),
    // 未登录
    UNAUTHORIZED(404, "未登录或token已过期"),
    // 操作频繁
    TOO_MANY_REQUESTS(405,"操作过于频繁，请稍后再试"),
    // 系统异常
    SYSTEM_ERROR(406, "服务器出小差了"),

    // 手机号格式错误
    PHONE_INVALID(4001,"手机号格式不正确（11位数字，1开头）"),
    // 验证码格式错误
    CODE_INVALID(4002,"验证码为6位数字"),
    // 密码格式错误
    PASSWORD_INVALID(4003,"密码为任意6到20位字符"),
    // 确认密码格式错误
    REPASSWORD_INVALID(4004,"密码为任意6到20位字符"),
    // 两次密码有误
    PASSWORD_MISMATCH(4005,"两次密码不一致"),
    // 账号已存在
    ACCOUNT_EXISTS(4006, "手机号已存在"),
    // 验证码错误
    CODE_MISMATCH(4007, "验证码错误"),
    // 账号不存在
    ACCOUNT_NOT_EXISTS(4008, "手机号未注册"),
    // 密码错误
    PASSWORD_NOT(4009, "密码错误"),
    // 密码未设置，请前往验证码登录
    PASSWORD_NOT_SET(4010, "密码未设置，请前往验证码登录");

    private final Integer code;
    private final String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}