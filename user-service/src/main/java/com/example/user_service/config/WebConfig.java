package com.example.user_service.config;

import com.example.user_service.common.JwtUtil;
import com.example.user_service.common.UserStatusInterceptor;
import com.example.user_service.common.UserStatusManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserStatusManager userStatusManager;
    private final JwtUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserStatusInterceptor(userStatusManager, jwtUtil))
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/oauth/**", "/images/**");// 인증이나 정적 자원 경로 제외
    }
}
