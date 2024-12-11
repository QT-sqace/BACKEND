package com.example.team_service.client;

import com.example.team_service.config.FeignClientConfig;
import com.example.team_service.dto.external.BasicInfoDto;
import com.example.team_service.dto.external.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service-client", url = "user-service.spring-boot-app.svc.cluster.local:8080", configuration = FeignClientConfig.class)
public interface UserServiceClient {
    //유저서비스를 통해서 유저 정보 가져옴(닉네임)
    @GetMapping("/basic/{user_id}")
    BasicInfoDto getUserBasicInfo(@PathVariable("user_id") Long userId);

    //팀 리스트 조회에서 유저 이미지 조회, 팀생성 시점에 생성자 프로필 이미지 조회
    @GetMapping("/profile/{userId}")
    String getUserProfile(@PathVariable("userId") Long userId);

    //팀 마이페이지에서 회원정보 가져오기 - 이름, 컨텍이메일, 폰번호, 프로필
    @GetMapping("/userInfo/{userId}")
    UserInfoDto getUserInfo(@PathVariable("userId") Long userId);
}
