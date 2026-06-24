package com.ai.interceptor;

import com.ai.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    // 构造器注入，Spring 会自动装配
    public TokenInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // 放行 CORS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();

        // 放行白名单接口
        if (isWhitelistedPath(requestURI)) {
            return true;
        }

        // 提取 Access Token
        String accessToken = getAccessTokenFromRequest(request);
        Cookie[] cookies = request.getCookies();
        if (accessToken == null || accessToken.isEmpty()) {
            String refreshToken = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
            try {
                jwtUtil.parseAccessToken(refreshToken);
                return true;
            }catch (Exception e){
                return sendUnauthorizedResponse(response);
            }
        } else {
            // 验证并解析
            try {
                Claims claims = jwtUtil.parseAccessToken(accessToken);
                String userId = claims.getSubject();
                request.setAttribute("userId", userId);
                return true;
            } catch (Exception e) {
                return sendUnauthorizedResponse(response);
            }
        }
    }

    private boolean isWhitelistedPath(String requestURI) {
        return requestURI.equals("/api/user/code") ||
                requestURI.equals("/api/user/phoneLogin") ||
                requestURI.equals("/api/user/accountLogin") ||
                requestURI.equals("/api/user/sendRegisterCode") ||
                requestURI.equals("/api/user/token") ||
                requestURI.equals("/api/user/logout");
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"error\":\"%s\"}", "Invalid token"));
        return false;
    }
}