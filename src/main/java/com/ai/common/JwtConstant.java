package com.ai.common;

public final class JwtConstant {
    private JwtConstant() {}

    // 刷新令牌时间
    public static final int REFRESH_TOKEN_DAYS = 7;
    public static final int REFRESH_TOKEN_MAX_AGE = REFRESH_TOKEN_DAYS * 24 * 60 * 60;

    //  Access Token有效期1天
    public static final long ACCESS_EXPIRATION = 24*60*60*1000L;

    //  Refresh Token有效期7天
    public static final long REFRESH_EXPIRATION = 7*24*60*60*1000L;
}
