package com.ai.interceptor;

import com.ai.util.RedisLockUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class ApiLockInterceptor implements HandlerInterceptor {

    private final RedisLockUtil redisLockUtil;

    // 构造器注入
    public ApiLockInterceptor(RedisLockUtil redisLockUtil) {
        this.redisLockUtil = redisLockUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        // 从 TokenInterceptor 获取注入的 userId
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return true;
        }

        // 锁的 key：userId + 请求 URI
        String lockKey = userId + ":" + request.getRequestURI();

        // 尝试获取分布式锁（10秒超时自动释放）
        String lockValue = redisLockUtil.tryLock(lockKey, 10, TimeUnit.SECONDS);
        if (lockValue == null) {
            // 未获取到锁，返回 429 Too Many Requests
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"msg\":\"操作频繁，请稍后再试\"}");
            return false;
        }

        // 把锁的 key 存入 request，供 afterCompletion 释放
        request.setAttribute("lockKey", lockKey);
        request.setAttribute("lockValue", lockValue);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        // 不管业务是否抛异常，都执行解锁
        String lockKey = (String) request.getAttribute("lockKey");
        String lockValue = (String) request.getAttribute("lockValue");
        if (lockKey != null && lockValue != null) {
            redisLockUtil.release(lockKey, lockValue);
        }
    }
}