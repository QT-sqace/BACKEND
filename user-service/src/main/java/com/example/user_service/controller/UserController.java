package com.example.user_service.controller;

import com.example.user_service.common.JwtUtil;
import com.example.user_service.dto.request.auth.ProfileResponseDto;
import com.example.user_service.dto.request.auth.UpdateProfileRequestDto;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final JwtUtil jwtUtil;
    private final UserService userService;


    //마이페이지 반환
    @GetMapping("/user/profile")
    public ResponseEntity<ProfileResponseDto> getProfile(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromHeader(token);
        log.info("확인용도 userId: {}",userId);
        ProfileResponseDto profile = userService.getProfile(userId);

        return ResponseEntity.ok(profile);
    }

    @PutMapping(value = "/user/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateProfile(
            @RequestHeader("Authorization") String token,
            @ModelAttribute UpdateProfileRequestDto requestDto) {
        Long userId = jwtUtil.extractUserIdFromHeader(token);

        userService.updateProfile(userId, requestDto);

        //반환 수정하기 JSON으로
        return ResponseEntity.ok("{\"message\": \"회원정보가 수정되었습니다.\"}");
    }
}
