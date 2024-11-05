package com.example.user_service.controller;

import com.example.user_service.dto.request.auth.CheckCertificationRequestDto;
import com.example.user_service.dto.request.auth.EmailCertificationRequestDto;
import com.example.user_service.dto.request.auth.SignInRequestDto;
import com.example.user_service.dto.request.auth.SignUpRequestDto;
import com.example.user_service.dto.response.ResponseDto;
import com.example.user_service.dto.response.auth.CheckCertificationResponseDto;
import com.example.user_service.dto.response.auth.EmailCertificationResponseDto;
import com.example.user_service.dto.response.auth.SignInResponseDto;
import com.example.user_service.dto.response.auth.SignUpResponseDto;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.example.user_service.provider.JwtProvider;
import com.example.user_service.repository.UserInfoRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;

    //requestbody로 프론트가 보낸 json 데이터 dto 객체로 매핑
    //valid 어노테이션은 dto 에서 정의한 유효성 검사 조건 확인
    //인증 메일 보내기
    @PostMapping("/auth/email-certification")
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(
            @RequestBody @Valid EmailCertificationRequestDto requestBody
    ) {
        ResponseEntity<? super EmailCertificationResponseDto> response = authService.emailCertification(requestBody);
        return response;
    }

    //인증 메일 번호 확인
    @PostMapping("/auth/check-certification")
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(
            @RequestBody @Valid CheckCertificationRequestDto requestBody) {
        ResponseEntity<? super CheckCertificationResponseDto> response = authService.checkCertification(requestBody);
        return response;
    }

    //회원 가입
    @PostMapping("/auth/sign-up")
    public ResponseEntity<? super SignUpResponseDto> signUp(
            @RequestBody @Valid SignUpRequestDto requestBody) {
        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestBody);
        return response;
    }

    //로그인
    @PostMapping("/auth/sign-in")
    public ResponseEntity<? super SignInResponseDto> signIn(
            @RequestBody @Valid SignInRequestDto requestBody) {
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestBody);
        return response;
    }

    //임시 jwt 확인용
    @GetMapping("/user/check")
    public ResponseEntity<ResponseDto> check() {
        // 인증된 사용자 정보 확인
        return ResponseEntity.ok(new ResponseDto("SUCCESS", "User is authenticated with ROLE_USER."));
    }

    //카톡 소셜 로그인
    @GetMapping("/auth/social/kakao")
    public ResponseEntity<SignInResponseDto> handleSocialLoginCallback(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);

        if (user == null || userInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null); // 사용자 정보가 없을 경우 401 응답
        }

        // JWT 토큰 생성
        String accessToken = jwtProvider.create(String.valueOf(userId), "kakao");

        // 사용자 정보를 포함한 응답 생성
        SignInResponseDto responseDto = SignInResponseDto.success(accessToken, user, userInfo).getBody();
        return ResponseEntity.ok(responseDto);
    }
}
