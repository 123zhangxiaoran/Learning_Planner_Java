/**
 * 封装Redis常用操作
 */
package com.ai.util;

public class RedisUtil {
    //  验证码有效期
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    //  验证码60秒冷却锁
    public static final String LOGIN_COOL_KEY = "login:cool:";
    public static final Long LOGIN_COOL_TTL = 60L;
    //  用户已存在的身份验证
    public static final String PLAYER_EXIST_KEY = "player:exist:";
    public static final Long PLAYER_EXIST_KEY_TTL = 259200L;
    //  注册占坑锁
    public static final String REGISTER_LOCK_KEY = "player:register:lock:";
    public static final Long REGISTER_LOCK_KEY_TTL = 5L;
    //  用户不存在
    public static final String PLAYER_EMPTY_PREFIX = "player:empty:phone:";
    public static final Long PLAYER_EMPTY_PREFIX_TTL = 60L;
    //  用户存在封禁锁
    public static final String PLAYER_COOL_KEY = "player:cool:";
    public static final Long PLAYER_COOL_KEY_TTL = 30L;
    //  退出无用token
    public static final String ACCESS_TOKEN_KEY = "token:jti:";
    //  api请求锁
    public static final String LOCK_PREFIX = "api_lock:";
}
