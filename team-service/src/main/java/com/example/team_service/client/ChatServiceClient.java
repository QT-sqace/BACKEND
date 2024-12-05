package com.example.team_service.client;

import com.example.team_service.dto.external.ChatParticipantAddRequestDto;
import com.example.team_service.dto.external.TeamChatRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chat-service-client", url = "http://localhost:8000/chatservice")
public interface ChatServiceClient {
    //팀 생성 시점에 채팅방 생성 요청하기
    @PostMapping("/create/room")
    void createRoom(@RequestBody TeamChatRequestDto requestDto);

    //팀원 추가시에 채팅방 가입 요청
    @PostMapping("/add/participant")
    void addParticipant(@RequestBody ChatParticipantAddRequestDto requestDto);
}
