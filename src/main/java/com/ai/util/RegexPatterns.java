package com.ai.util;

public abstract class RegexPatterns {
    /**
     * 手机号正则
     */
    public static final String PHONE_REGEX = "^1(3[0-9]|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|9[0-35-9])\\d{8}$";
    /**
     * 密码正则。6~20位的字母、数字、下划线
     */
    public static final String PASSWORD_REGEX = "^\\w{6,20}$";
    /**
     * 验证码正则, 6位数字或字母
     */
    public static final String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";
}
