/**
 * 跨域，拦截器，静态资源映射
 */
package com.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // 你后续新建的锁拦截器
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ai.interceptor.ApiLockInterceptor;
import com.ai.interceptor.TokenInterceptor;

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
                .allowedOrigins("http://localhost:5173","http://localhost:3000","http://192.168.101.28:3000/")
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
                        "/user/code",
                        "/user/phoneLogin",
                        "/user/accountLogin",
                        "/user/sendRegisterCode"
                );

        // 2. 锁拦截器
        registry.addInterceptor(apiLockInterceptor)
                .order(2)
                .addPathPatterns("/**");
    }
}