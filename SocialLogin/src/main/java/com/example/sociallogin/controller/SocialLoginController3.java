package com.example.sociallogin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Optional;
/**
 * 아래 uri들은 각 소셜 로그인 제공자의 로그인 성공시 리다이렉트 되는 uri
 * 유저 정보를 제대로 받아오는지 테스트를 위한 컨트롤러로 실제 서비스에서는 사용하지 않음
 * */
@Controller
public class SocialLoginController {
    private static final Logger log = LoggerFactory.getLogger(SocialLoginController.class);

    @GetMapping("/google")
    public String GooglePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User google_useer_info = (OAuth2User) authentication.getPrincipal();
        log.info("LoginResponse: {}", google_useer_info);
        model.addAllAttributes(new HashMap<>() {{
            put("name", google_useer_info.getName());
            put("email", google_useer_info.getAttribute("email"));
        }});
        return "my";
    }

    @GetMapping("/kakao")
    public String KakaoPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User kakao_user_info = (OAuth2User) authentication.getPrincipal();
        log.info(kakao_user_info.getAttributes().toString());
        log.info("LoginResponse: {}", kakao_user_info);
        log.info("nonce: {}", Optional.ofNullable(kakao_user_info.getAttribute("nonce")));
        model.addAllAttributes(new HashMap<>() {{
            put("nickName", kakao_user_info.getAttribute("profile_nickname"));
        }});
        return "my";
    }
}
