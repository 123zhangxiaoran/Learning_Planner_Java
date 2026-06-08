package com.ai.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync   // 开启异步支持
public class AsyncConfig {

    @Bean(name = "questionExecutor")
    public Executor questionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);          // 核心线程数
        executor.setMaxPoolSize(20);          // 最大线程数
        executor.setQueueCapacity(200);       // 队列容量
        executor.setThreadNamePrefix("async-question-"); // 线程名称前缀
        executor.initialize();
        return executor;
    }
}