package com.poc.observability.metrics.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.UUID;

@Configuration
public class LoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(2048);
        filter.setIncludeHeaders(false);
        filter.setBeforeMessagePrefix("INFO Request received: ");
        filter.setAfterMessagePrefix("INFO Request completed: ");
        return filter;
    }

    public static void setRequestContext() {
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("timestamp", String.valueOf(System.currentTimeMillis()));
    }
}
