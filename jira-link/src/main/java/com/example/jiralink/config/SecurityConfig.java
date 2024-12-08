package com.example.jiralink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로
                        .requestMatchers("/jira/auth/**").permitAll()
                        // 인증이 필요한 경로
                        .requestMatchers("/jira/projects/**").permitAll()
                        .requestMatchers("/jira/teams/**").permitAll()
                        .requestMatchers("/jira/**").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin().disable();
//                .formLogin(form -> form
//                        .loginPage("/jira/auth/login") // 기본 로그인 경로를 변경
//                        .permitAll()
//                );

        return http.build();
    }
}