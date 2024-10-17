package com.example.sociallogin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 아래 uri들은 각 소셜 로그인 제공자의 로그인 성공시 리다이렉트 되는 uri
 * 유저 정보를 제대로 받아오는지 테스트를 위한 컨트롤러로 실제 서비스에서는 사용하지 않음
 * */
@RestController
public class SocialLoginController {
    private static final Logger log = LoggerFactory.getLogger(SocialLoginController.class);

    @GetMapping("/google")
    public @ResponseBody Map<String, String> GooglePage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User google_user_info = (OAuth2User) authentication.getPrincipal();
        log.info("LoginResponse: {}", google_user_info);

        Map<String, String> response = new HashMap<>();
//        response.put("name", google_user_info.getName()); 중간때는 email만 활용
        response.put("email", google_user_info.getAttribute("email"));
        return response; // JSON 형식으로 반환
    }

    @GetMapping("/kakao")
    public @ResponseBody Map<String, String> KakaoPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User kakao_user_info = (OAuth2User) authentication.getPrincipal();
        log.info(kakao_user_info.getAttributes().toString());
        log.info("LoginResponse: {}", kakao_user_info);
        log.info("nonce: {}", Optional.ofNullable(kakao_user_info.getAttribute("nonce")));

        Map<String, String> response = new HashMap<>();
        response.put("email", kakao_user_info.getAttribute("email"));
        return response; // JSON 형식으로 반환
    }
}
