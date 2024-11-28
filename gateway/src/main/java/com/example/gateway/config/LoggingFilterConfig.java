package com.example.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class LoggingFilterConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilterConfig.class);

    @Bean
    public GlobalFilter logRequestFilter() {
        return (exchange, chain) -> {
            logger.info("Routing request: Method={} URI={}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    logger.info("Response sent for URI={}", exchange.getRequest().getURI())
            ));
        };
    }
}