package com.example.user_service.handler;

import com.example.user_service.entity.CustomOAuth2User;
import com.example.user_service.provider.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String providerId = oAuth2User.getName();
        String provider = oAuth2User.getAttributes().containsKey("sub") ? "google" : "kakao";
        //여기 수정함
        String accessToken = jwtProvider.create(providerId, provider);

        log.info("accessToken 값: " + accessToken);
//        response.sendRedirect("http://localhost:3000/auth/oauth-response/" + accessToken + "/3600");
/*
        // JSON으로 반환 -> 리다이렉트를 못시키는 문제
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\", \"expirationTime\": " + expirationTime + "}");
*/
        int expirationTime = 43200;
        // JWT를 쿠키에 저장
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true); // JavaScript에서 접근하지 못하도록 설정
        cookie.setMaxAge(expirationTime); // 만료 시간 설정 (초 단위)
        cookie.setPath("/"); // 모든 경로에서 사용할 수 있도록 설정

        response.addCookie(cookie); // 쿠키 추가

        // 리다이렉트 URL 설정 (프론트엔드에서 정의한 리다이렉트 URL로 설정)-> 나중에 서버 올리면 수정
        response.sendRedirect("http://localhost:3000/"); // 예시 URL, 실제 사용하려는 URL로 수정

    }
}
