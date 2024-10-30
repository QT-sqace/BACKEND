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
import java.util.Optional;

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
/*
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
        }*/

        // 사용자 정보를 DB에 저장하는 부분
        try {
            // 이메일이 이미 존재하는지 확인
            Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(userEntity.getEmail()));

            if (existingUser.isPresent()) {
                User user = existingUser.get();

                // 이메일로 가입된 사용자이고, 소셜 로그인 제공자가 다르면 충돌 처리
                if ("email".equals(user.getProvider())) {
                    log.warn("이메일 " + userEntity.getEmail() + "로 이미 이메일 가입된 사용자입니다. 이메일 로그인 요청.");
                    throw new IllegalArgumentException("이 이메일은 이미 등록되어 있습니다. 이메일과 비밀번호로 로그인해 주세요.");
                } else {
                    // 소셜 로그인 제공자가 다르면 경고
                    if (!user.getProvider().equals(userEntity.getProvider())) {
                        log.warn("이메일 " + userEntity.getEmail() + "로 이미 " + user.getProvider() + "로 가입된 사용자입니다. " + userEntity.getProvider() + "로 로그인하려면 다른 이메일을 사용해 주세요.");
                        throw new IllegalArgumentException("이 이메일은 이미 " + user.getProvider() + "로 등록되어 있습니다. " + userEntity.getProvider() + "로 로그인하려면 다른 이메일을 사용해 주세요.");
                    } else {
                        // 같은 소셜 로그인 제공자일 경우 소셜 로그인 진행
                        log.info("이메일 " + userEntity.getEmail() + "로 소셜 로그인 성공.");
                    }
                }
            } else {
                // 이메일이 존재하지 않으면 새로운 소셜 로그인 사용자 저장
                userRepository.save(userEntity);
                log.info("이메일 " + userEntity.getEmail() + "로 새로운 소셜 로그인 사용자 저장.");
            }
        } catch (Exception e) {
            log.error("사용자를 데이터베이스에 저장하는 중 오류 발생: " + e.getMessage());
        }


        //여기 수정
        return new CustomOAuth2User(providerId, attributes);
    }
}
