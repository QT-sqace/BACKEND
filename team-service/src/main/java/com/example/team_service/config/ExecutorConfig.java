package com.example.team_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    //팀생성시 팀캘린더로 비동기 처리를 위한 config

    @Bean
    public ExecutorService executorService() {
        //fixed thread pool 사용 (스레드 개수 제한)
        return Executors.newFixedThreadPool(10);
    }
}
