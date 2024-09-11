package com.vipa.medlabel.config.retryconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
    // 暂时不配置复杂重试策略
}
