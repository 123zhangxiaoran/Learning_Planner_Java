package com.ai.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_PREFIX = "api_lock:";

    // 释放锁的 Lua 脚本（原子操作：值匹配才删除）
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";

    private final DefaultRedisScript<Long> unlockScript;

    public RedisLockUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 提前编译脚本，提高性能
        this.unlockScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
    }

    /**
     * 尝试获取分布式锁（非阻塞）
     *
     * @param key       锁的业务标识
     * @param leaseTime 锁的自动过期时间
     * @param unit      时间单位
     * @return 锁的唯一 value（用于安全释放），获取失败返回 null
     */
    public String tryLock(String key, long leaseTime, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, leaseTime, unit);
        return Boolean.TRUE.equals(success) ? lockValue : null;
    }

    /**
     * 释放分布式锁（只有 value 匹配时才删除）
     *
     * @param key           锁的业务标识
     * @param expectedValue 获取锁时返回的唯一标识
     */
    public void release(String key, String expectedValue) {
        String lockKey = LOCK_PREFIX + key;
        redisTemplate.execute(unlockScript,
                Collections.singletonList(lockKey),
                expectedValue);
    }
}