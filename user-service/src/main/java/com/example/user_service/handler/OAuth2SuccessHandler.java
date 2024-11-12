package com.example.user_service.handler;

import com.example.user_service.dto.response.auth.SignInResponseDto;
import com.example.user_service.entity.CustomOAuth2User;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.example.user_service.provider.JwtProvider;
import com.example.user_service.repository.UserInfoRepository;
import com.example.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    //소셜로그인 성공시 헨들러
    //OAuth2userService 이후 로그인 성공시 로직
    //Jwt 발급,
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {


        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = Long.valueOf(oAuth2User.getName());   //userId로 변환

        // registrationId를 통해 로그인 제공자를 구분
        String registrationId = oAuth2User.getRegistrationId();
        String redirectUrl;

        //연동 과정에 따라 다른 리다이렉트 실행 (반환값 다르게 하기 위함)
        if ("Google".equals(registrationId)) {
            log.info("구글 리다이렉트 실행!!!!!!!!!!!!!!!");
            redirectUrl = "http://localhost:3000/oauth/callback/google?userId=" + userId;
        } else if ("kakao".equals(registrationId)) {
            log.info("카카오 리다이렉트 실행!!!!!!!!!!!!!!!");
            redirectUrl = "http://localhost:3000/oauth/callback/kakao?userId=" + userId;
        } else {
            // 기본 리다이렉트 경로 설정
            log.info("기본 리다이렉트 실행!!!!!!!!!!!!!!!");
            redirectUrl = "http://localhost:3000/oauth/callback?userId=" + userId;
        }

        response.sendRedirect(redirectUrl);


    }
}
