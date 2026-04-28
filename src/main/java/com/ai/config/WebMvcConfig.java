/**
 * 跨域，拦截器，静态资源映射
 */
package com.ai.config;

import com.ai.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    public WebMvcConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    /**
                     * 请求前拦截
                     */
                    @Override
                    public boolean preHandle(
                            @NonNull HttpServletRequest request,
                            @NonNull HttpServletResponse response,
                            @NonNull Object handler
                    ) throws Exception {
                        // 放行CORS预检请求
                        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                            return true;
                        }

                        String requestURI = request.getRequestURI();

                        // 放行白名单接口（登录、注册、发送验证码等）
                        if (isWhitelistedPath(requestURI)) {
                            return true;
                        }

                        // 从请求头提取Access Token
                        String accessToken = getAccessTokenFromRequest(request);

                        // Token缺失
                        if (accessToken == null || accessToken.isEmpty()) {
                            return sendUnauthorizedResponse(response, "Token required");
                        }

                        // 验证Token签名
                        try {
                            Claims claims = WebMvcConfig.this.jwtUtil.parseAccessToken(accessToken);
                            String userId = claims.getSubject();
                            // 将userId存入请求属性，供后续Controller使用
                            request.setAttribute("userId", userId);
                            return true;
                        } catch (Exception e) {
                            // Token无效或已过期
                            return sendUnauthorizedResponse(response, "Invalid token");
                        }
                    }

                    /**
                     * 检查请求路径是否在白名单中
                     */
                    private boolean isWhitelistedPath(String requestURI) {
                        return requestURI.equals("/api/user/code") ||
                                requestURI.equals("/api/user/phoneLogin") ||
                                requestURI.equals("/api/user/accountLogin") ||
                                requestURI.equals("/api/user/sendRegisterCode") ||
                                requestURI.equals("/api/user/token") ||
                                requestURI.equals("/api/user/logout");
                    }

                    /**
                     * 从请求头获取Access Token
                     * 格式：Authorization: Bearer {token}
                     */
                    private String getAccessTokenFromRequest(HttpServletRequest request) {
                        String authHeader = request.getHeader("Authorization");

                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            // 去掉 "Bearer " 前缀（7个字符），返回纯token
                            return authHeader.substring(7);
                        }

                        return null;
                    }

                    /**
                     * 发送401未授权响应
                     */
                    private boolean sendUnauthorizedResponse(HttpServletResponse response, String errorMessage) throws IOException {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(String.format("{\"error\":\"%s\"}", errorMessage));
                        return false;
                    }
                })
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/code",
                        "/api/user/phoneLogin",
                        "/api/user/accountLogin",
                        "/api/user/sendRegisterCode"
                );
    }


}