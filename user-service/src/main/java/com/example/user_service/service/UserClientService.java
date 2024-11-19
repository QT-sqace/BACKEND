package com.example.user_service.service;

import com.example.user_service.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClientService {
    //유저 페인클라이언트 처리 서비스

    private final UserInfoRepository userInfoRepository;

    public String getProfileImage(Long userId) {
        //userId로 user_info 테이블에서 조회
        return userInfoRepository.findById(userId)
                .map(userInfo -> userInfo.getProfileImage())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
    }
}
