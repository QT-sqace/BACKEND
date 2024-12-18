package com.example.team_service.client;

import com.example.team_service.dto.external.ChatParticipantAddRequestDto;
import com.example.team_service.dto.external.ChatParticipantDeleteRequestDto;
import com.example.team_service.dto.external.TeamChatRequestDto;
import com.example.team_service.dto.external.UpdateTeamNameRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chat-service-client", url = "http://chat-service.spring-boot-app.svc.cluster.local:8083/chatservice")
public interface ChatServiceClient {
    //팀 생성 시점에 채팅방 생성 요청하기
    @PostMapping("/create/room")
    void createRoom(@RequestBody TeamChatRequestDto requestDto);

    //팀원 추가시에 채팅방 가입 요청
    @PostMapping("/add/participant")
    void addParticipant(@RequestBody ChatParticipantAddRequestDto requestDto);

    //팀 추방시에 채팅방 자동 퇴장 요청
    @PostMapping("/delete/participant")
    void deleteParticipant(@RequestBody ChatParticipantDeleteRequestDto requestDto);

    //팀 이름 변경시 채팅방 이름변경 요청
    @PutMapping("/update/teamName")
    void updateTeamName(@RequestBody UpdateTeamNameRequestDto requestDto);
}
