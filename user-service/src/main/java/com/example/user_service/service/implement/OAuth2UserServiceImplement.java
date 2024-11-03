package com.example.user_service.service.implement;

import com.example.user_service.entity.CustomOAuth2User;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.example.user_service.repository.UserInfoRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImplement extends DefaultOAuth2UserService {


    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;

    //이걸 webSecurity에 등록
    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        String oauthClientName = request.getClientRegistration().getClientName();
        OAuth2User oAuth2User = super.loadUser(request);

        String providerId = null;
        String email = null;
        String nickname = null;
        String provider = null;
        Long userId = null;
//        Map<String, Object> attributes = null;
        Map<String, Object> attributes = oAuth2User.getAttributes();    //가져오는 정보
        log.info("가져오는 값 확인: "+attributes.toString());

        //카카오
        if (oauthClientName.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
            provider = "kakao";
            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
            log.info("Kakao에서 가져오는값 정리 이메일: "+email + " id: " + providerId + " 닉네임: " + nickname);
        }

        //구글
        if (oauthClientName.equals("Google")) {
            providerId = String.valueOf(oAuth2User.getAttributes().get("sub"));
            //여기 수정
            email = (String) oAuth2User.getAttributes().get("email");
            nickname = (String) oAuth2User.getAttributes().get("name");
            provider = "google";
            log.info("google에서 가져오는값 정리 이메일: " + email + " id: " + providerId);
        }
        //사용자 정보 저장 및 업데이트
        Optional<User> existingUserOpt = Optional.ofNullable(userRepository.findByEmail(email));
        User userEntity;

        try {
            if (existingUserOpt.isPresent()) {
                userEntity = existingUserOpt.get();
                if (!userEntity.getProvider().equals(provider)) {
                    throw new IllegalArgumentException("이미 다른 제공자로 가입된 이메일입니다.");
                }
                userId = userEntity.getUserId();
            } else {
                userEntity = new User(providerId, email, provider);
                userEntity = userRepository.save(userEntity);   //저장 후 userEntity를 영속성 컨텍스트에 반영
                userId = userEntity.getUserId();
                log.info("새로운 사용자 저장 완료 - userId: {}" , userId);

                LocalDateTime createdAt = LocalDateTime.now();
                UserInfo userInfo = new UserInfo(userEntity, nickname, createdAt);
                userInfoRepository.save(userInfo);
                log.info("UserInfo 저장 완료 - userId: {}, 닉네임: {}" , userInfo.getUserId(), userInfo.getUserName());
            }
        } catch (Exception e) {
            log.error("사용자 정보 저장 중 오류 발생: " + e.getMessage());
            throw new OAuth2AuthenticationException("사용자 정보 저장 실패");
        }

        //여기 수정
        return new CustomOAuth2User(String.valueOf(userId), attributes);
    }
}
