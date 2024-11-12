package com.example.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //나중에 minIO로 바꾸면 이미지 프로필 가져가는 경로 수정해야함
    //나중에 webSecurityConfig쪽에서도 사용해서 이미지 경로 오류나면 확인
    @Value("${profile.image.path}")
    private String profileImagePath;

    @Value("${profile.image.url}")
    private String profileImageUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //src/main/resources/images/user/에 저장된 이미지를 /images/user/** 경로로 접근 가능하게 설정
        //각각 profileImageUrl, profileImagePath 로 지정
        registry.addResourceHandler(profileImageUrl+"**")
                .addResourceLocations(profileImagePath);
    }
}
