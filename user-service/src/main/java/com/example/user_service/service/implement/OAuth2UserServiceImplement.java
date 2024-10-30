package com.example.user_service.service.implement;

import com.example.user_service.entity.CustomOAuth2User;
import com.example.user_service.entity.User;
import com.example.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {


    private final UserRepository userRepository;

    //이걸 webSecurity에 등록
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        log.info("loadUser 호출되었음11111111111111111111111111111111111111");
        String oauthClientName = request.getClientRegistration().getClientName();
        log.info("OAuth 클라이언트 이름: "+oauthClientName);

        //여기서 유저 정보 가져옴
        OAuth2User oAuth2User = super.loadUser(request);
        log.info(oAuth2User.getName());

        try {
            System.out.println(new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
            log.info(new ObjectMapper().writeValueAsString(oAuth2User.getName()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        User userEntity = null;
        String providerId = null;
        String email = null;
        Map<String, Object> attributes = null;

        if (oauthClientName.equals("kakao")) {
            providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
            //여기 수정
            attributes = oAuth2User.getAttributes();
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
            userEntity = new User(providerId, email, "kakao");
            log.info("Kakao에서 가져오는값 정리 이메일: "+email + " id: " + providerId);
        }

        if (oauthClientName.equals("Google")) {
            providerId = String.valueOf(oAuth2User.getAttributes().get("sub"));
            //여기 수정
            attributes = oAuth2User.getAttributes();
            email = (String) oAuth2User.getAttributes().get("email");
            userEntity = new User(providerId, email, "google");
            log.info("google에서 가져오는값 정리 이메일: " + email + " id: " + providerId);
        }

// 사용자 정보를 DB에 저장하는 부분
        try {
            // 이메일이 이미 존재하는지 확인
            if (!userRepository.existsByEmail(userEntity.getEmail())) {
                userRepository.save(userEntity);
            } else {
                log.warn("User with email " + userEntity.getEmail() + " already exists. Skipping save.");
            }
        } catch (Exception e) {
            log.error("Error saving user to database: " + e.getMessage());
        }

        //여기 수정
        return new CustomOAuth2User(providerId, attributes);
    }
}
