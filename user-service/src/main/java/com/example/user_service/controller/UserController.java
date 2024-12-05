package com.example.user_service.controller;

import com.example.user_service.common.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {
    private final JwtUtil jwtUtil;

    public UserController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    //마이페이지 수정 - 임시

    @GetMapping("/user/profile")
    public ResponseEntity<String> getProfile(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserIdFromHeader(token);
        log.info("확인용도 userId: {}",userId);

        return ResponseEntity.ok("확인용도");
    }
}
