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

//        setRedirectStrategy((request1, response1, url) -> {});//리다이렉트 막기

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = Long.valueOf(oAuth2User.getName());   //userId로 변환

        //이부분 변경
        String redirectUrl = "http://localhost:3000/oauth/callback?userId=" + userId;
        response.sendRedirect(redirectUrl);

/*
        User user = userRepository.findById(userId).orElse(null);
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
        System.out.println("userInfo.userName: " + (userInfo != null ? userInfo.getUserName() : "No UserInfo found"));

        if (user == null || userInfo == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found.");
            return;
        }


        String provider = oAuth2User.getAttributes().containsKey("sub") ? "google" : "kakao";
        String accessToken = jwtProvider.create(String.valueOf(userId), provider);

        //이메일 로그인과 동일한 형식으로 json 응답 생성
        SignInResponseDto responseDto = SignInResponseDto.success(accessToken, user, userInfo).getBody();

//        log.info("반환되는 시간 형식 확인용도:111111111111111111" +responseDto.getCreatedAt().toString());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8"); // UTF-8 인코딩 강제 설정
        response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        response.setStatus(HttpServletResponse.SC_OK);
*/

    }
}
