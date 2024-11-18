package com.example.user_service.service.implement;

import com.example.user_service.client.CalendarServiceClient;
import com.example.user_service.dto.external.UserCalendarRequestDto;
import com.example.user_service.entity.CustomOAuth2User;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.example.user_service.repository.UserInfoRepository;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final CalendarServiceClient calendarServiceClient;

    //기본 프로필 경로
    @Value("${profile.image.default}")
    private String defaultProfileImage;

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

        //accessToken
        String loginAccessToken = request.getAccessToken().getTokenValue();
        log.info("로그인 소셜 엑세스 토큰: " + loginAccessToken);

        //refresh토큰 null 체크
        Object loginRefreshTokenObj = request.getAdditionalParameters().get("refresh_token");
        String loginRefreshToken = loginRefreshTokenObj != null ? loginRefreshTokenObj.toString() : null;

        // 현재 시간, 토큰 만료시간, 남은 만료시간 계산
        Instant now = Instant.now();
        Instant expiresAtLoginAccessTokenObj = request.getAccessToken().getExpiresAt();
        long expiresAtLoginAccessToken = expiresAtLoginAccessTokenObj.getEpochSecond() - now.getEpochSecond();
        log.info("엑세스 토큰 유효 기간(초): " + expiresAtLoginAccessToken);


        //카카오 또는 구글의 속성 정보 확인 및 처리
        Map<String, Object> attributes = oAuth2User.getAttributes();    //가져오는 정보
        log.info("가져오는 값 확인: "+attributes.toString());

        // 카카오
        if (oauthClientName.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

            providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
            provider = "kakao";
            email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            nickname = profile != null ? (String) profile.get("nickname") : "Unknown"; // 닉네임이 없을 경우 기본값 설정

            log.info("Kakao에서 가져오는 값 정리 - 이메일: " + email + " id: " + providerId + " 닉네임: " + nickname);
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

        //provider와 providerId로 중복 검증 수행
        Optional<User> existingUserOpt = userRepository.findByProviderAndProviderId(provider, providerId);
        User userEntity;

        //액세스토큰 추가뒤 변경 부분
        try {
            if (existingUserOpt.isPresent()) {
                //기존 회원이므로 accessToken과 refreshToken을 갱신
                userEntity = existingUserOpt.get();
                userId = userEntity.getUserId();

                //acessToken, refreshToken 및 만료 시간 업데이트
                userEntity.updateLoginTokens(loginAccessToken, loginRefreshToken, expiresAtLoginAccessToken);
                userRepository.save(userEntity);
                log.info("기존 사용자 토큰 업데이트 완료 - userId: " + userId);

            } else {
                //새로운 가입자
                userEntity = new User(providerId, email, provider);
                userEntity.updateLoginTokens(loginAccessToken, loginRefreshToken, expiresAtLoginAccessToken);
                userEntity = userRepository.save(userEntity);   //저장 후 userEntity를 영속성 컨텍스트에 반영
                userId = userEntity.getUserId();

                LocalDateTime createdAt = LocalDateTime.now();
                UserInfo userInfo = new UserInfo(userEntity, nickname, createdAt, defaultProfileImage);
                userInfoRepository.save(userInfo);
                //캘린더 서비스로 개인 캘린더 생성 요청 - 임시로 막음
//                UserCalendarRequestDto requestDto = new UserCalendarRequestDto(userEntity.getUserId());
//                calendarServiceClient.createPersonalCalendar(requestDto);

                log.info("새로운 사용자 저장 완료 - userId: {}" , userId);
                log.info("UserInfo 저장 완료 - userId: {}, 닉네임: {}" , userInfo.getUserId(), userInfo.getUserName());
                log.info("캘린더 생성요청 완료");
            }
        } catch (Exception e) {
            log.error("사용자 정보 저장 중 오류 발생: " + e.getMessage());
            throw new OAuth2AuthenticationException("사용자 정보 저장 실패");
        }

        //여기 수정
        return new CustomOAuth2User(String.valueOf(userId), attributes, oauthClientName);
    }
}
