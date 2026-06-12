package com.ai.interceptor;

import com.ai.common.ResponseCode;
import com.ai.util.RedisLockUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.util.PathMatcher;           // 🆕 新增导入
import org.springframework.util.AntPathMatcher;      // 🆕 新增导入

import java.util.concurrent.TimeUnit;
import java.util.List;                               // 🆕 新增导入
import java.util.Arrays;                            // 🆕 新增导入

@Component
public class ApiLockInterceptor implements HandlerInterceptor {

    private final RedisLockUtil redisLockUtil;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> WHITELIST_PATTERNS = Arrays.asList(
            "/api/user/submitQuestionAnswer",
            ""
    );

    // 构造器注入
    public ApiLockInterceptor(RedisLockUtil redisLockUtil) {
        this.redisLockUtil = redisLockUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        String requestUri = request.getRequestURI();
        if (isWhitelisted(requestUri)) {
            return true;
        }

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
            // 未获取到锁，返回 405
            response.setStatus(405);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(String.valueOf(ResponseCode.TOO_MANY_REQUESTS));
            return false;
        }

        // 把锁的 key，value 存入 request，供 afterCompletion 释放
        request.setAttribute("lockKey", lockKey);
        request.setAttribute("lockValue", lockValue);
        return true;
    }

    // 白名单匹配辅助方法
    private boolean isWhitelisted(String requestUri) {
        return WHITELIST_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        String lockKey = (String) request.getAttribute("lockKey");
        String lockValue = (String) request.getAttribute("lockValue");
        if (lockKey != null && lockValue != null) {
            redisLockUtil.release(lockKey, lockValue);
        }
    }
}