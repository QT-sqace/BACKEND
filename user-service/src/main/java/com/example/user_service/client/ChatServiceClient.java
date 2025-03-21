package com.example.user_service.client;

import com.example.user_service.dto.external.UpdateUserProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chat-service-client", url = "http://localhost:8000/chatservice")
public interface ChatServiceClient {

    @PutMapping("/updateProfile")
    void updateProfile(@RequestBody UpdateUserProfileDto requestDto);
}
