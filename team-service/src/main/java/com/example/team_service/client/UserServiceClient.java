package com.example.team_service.client;

import com.example.team_service.config.FeignClientConfig;
import com.example.team_service.dto.BasicInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service-client", url = "http://localhost:8000/userservice", configuration = FeignClientConfig.class)
public interface UserServiceClient {
    //유저서비스를 통해서 유저 정보 가져옴
    @GetMapping("/basic/{userId}")
    BasicInfoDto getUserBasicInfo(@PathVariable("userId") Long userId);
}
