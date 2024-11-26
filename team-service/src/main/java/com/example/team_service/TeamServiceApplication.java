package com.example.team_service;

import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class TeamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamServiceApplication.class, args);
	}

	// Feign 로깅 레벨 설정
	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}
