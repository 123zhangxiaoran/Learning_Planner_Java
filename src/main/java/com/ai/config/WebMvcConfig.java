/**
 * 跨域，拦截器，静态资源映射
 */
package com.ai.config;

import com.ai.interceptor.TokenInterceptor;
import com.ai.interceptor.ApiLockInterceptor; // 你后续新建的锁拦截器
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;
    private final ApiLockInterceptor apiLockInterceptor;

    public WebMvcConfig(TokenInterceptor tokenInterceptor,
                        ApiLockInterceptor apiLockInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
        this.apiLockInterceptor = apiLockInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173","http://192.168.101.28:5173/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    //  拦截器的设置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Token 拦截器
        registry.addInterceptor(tokenInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/code",
                        "/api/user/phoneLogin",
                        "/api/user/accountLogin",
                        "/api/user/sendRegisterCode"
                );

        // 2. 锁拦截器
        registry.addInterceptor(apiLockInterceptor)
                .order(2)
                .addPathPatterns("/**");
    }
}