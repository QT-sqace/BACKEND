package com.example.user_service.controller;

import com.example.user_service.dto.external.BasicInfoDto;
import com.example.user_service.dto.external.UserDetailResponseDto;
import com.example.user_service.dto.external.UserInfoDto;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.example.user_service.repository.UserInfoRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.UserClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class UserApiController {

    //이메일, 유저이름 반환하는 FeignClient 예시
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserClientService userService;

    @GetMapping("/basic/{userId}")
    public ResponseEntity<BasicInfoDto> getUserBasicInfo(@PathVariable("userId") Long userId) {
        // userId를 통해 User 엔티티에서 email 조회
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BasicInfoDto("User not found", ""));
        }
        String email = userOpt.get().getEmail();

        // userId를 통해 UserInfo 엔티티에서 userName 조회
        Optional<UserInfo> userInfoOpt = userInfoRepository.findById(userId);
        if (userInfoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BasicInfoDto("UserInfo not found", email));
        }
        String userName = userInfoOpt.get().getUserName();

        // BasicInfoDto에 userName과 email 설정하여 반환
        BasicInfoDto basicInfoDto = new BasicInfoDto(userName, email);
        return ResponseEntity.ok(basicInfoDto);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<String> getUserProfile(@PathVariable("userId") Long userId) {
        String profileImage = userService.getProfileImage(userId);
        return ResponseEntity.ok(profileImage);
    }

    @GetMapping("/userInfo/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable("userId") Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        UserInfoDto userInfoDto = new UserInfoDto(
                userInfo.getUserName() != null ? userInfo.getUserName() : "닉네임 설정 필요",
                userInfo.getContactEmail(),
                userInfo.getPhoneNumber() != null ? userInfo.getPhoneNumber() : "No phoneNumber",
                userInfo.getProfileImage() != null ? userInfo.getProfileImage() : "기본프로필 경로 반환 예정"
        );
        return ResponseEntity.ok(userInfoDto);
    }

    @GetMapping("/userDetailInfo/{userId}")
    public ResponseEntity<UserDetailResponseDto> getUserDetailInfo(@PathVariable("userId") Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        UserDetailResponseDto responseDto = new UserDetailResponseDto(
                userInfo.getUserName(),
                userInfo.getProfileImage()
        );
        return ResponseEntity.ok(responseDto);
    }
}
