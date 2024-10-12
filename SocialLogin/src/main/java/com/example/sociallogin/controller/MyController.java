package com.example.sociallogin.controller;

import com.example.sociallogin.Service.CustomOAuth2UserService;
import com.example.sociallogin.dto.CustomOAuth2User;
import com.example.sociallogin.dto.GoogleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class MyController {
    private static final Logger log = LoggerFactory.getLogger(MyController.class);
    private CustomOAuth2UserService customOAuth2UserService;

    @GetMapping("/google")
    public String GooglePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User customOAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("LoginResponse: {}", customOAuth2User);
        model.addAllAttributes(new HashMap<>() {{
            put("name", customOAuth2User.getName());
            put("email", customOAuth2User.getAttribute("email"));
        }});
        return "my";
    }

    @GetMapping("/kakao")
    public String KakaoPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User customOAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("LoginResponse: {}", customOAuth2User);
        log.info("nonce: {}", Optional.ofNullable(customOAuth2User.getAttribute("nonce")));
        model.addAllAttributes(new HashMap<>() {{
            put("nickName", customOAuth2User.getAttribute("profile_nickname"));
        }});
        return "my";
    }
}
