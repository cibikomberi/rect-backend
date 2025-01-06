package com.rect.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // Number of core threads
        executor.setMaxPoolSize(1000); // Maximum number of threads
        executor.setQueueCapacity(10000); // Queue capacity
        executor.setThreadNamePrefix("BuildThread-");
        executor.initialize();
        return executor;
    }
}
