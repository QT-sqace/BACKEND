package com.example.chat_service.controller;

import com.example.chat_service.dto.external.*;
import com.example.chat_service.dto.request.ChatMessageDto;
import com.example.chat_service.dto.response.ChatRoomDataResponseDto;
import com.example.chat_service.dto.response.ChatRoomDto;
import com.example.chat_service.repository.ChatRoomRepository;
import com.example.chat_service.service.ChatRoomService;
import com.example.chat_service.service.TeamChatRoomService;
import com.example.chat_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamChatRoomController {

    private final TeamChatRoomService chatService;
    private final JwtUtil jwtUtil;
    private final ChatRoomService chatRoomService;

    //팀 채팅방 생성 요청
    @PostMapping("/create/room")
    public ResponseEntity<String> createTeamRoom(@RequestBody TeamChatRequestDto requestDto) {
        chatService.createRoom(requestDto);
        chatRoomService.updateUserChatRoomsInRedis(requestDto.getCreatorUserId());
        log.info("팀 채팅방 생성 성공");
        return ResponseEntity.ok("팀 채팅방 생성 성공");
    }

    //팀 채팅방 팀원 추가 요청
    @PostMapping("/add/participant")
    public ResponseEntity<String> addParticipant(@RequestBody ChatParticipantAddRequestDto requestDto) {
        chatService.addParticipant(requestDto);
        chatRoomService.updateUserChatRoomsInRedis(requestDto.getUserId());
        return ResponseEntity.ok("팀채팅방 팀원 추가 성공");
    }

    //팀 채팅방 추방요청
    @PostMapping("/delete/participant")
    public ResponseEntity<String> deleteParticipant(@RequestBody ChatParticipantDeleteRequestDto requestDto) {
        chatService.deleteParticipant(requestDto);

        return ResponseEntity.ok("팀 채팅방에서 추방 성공");
    }

    //팀 채팅방 이름변경 요청 - 페인
    @PutMapping("/update/teamName")
    public ResponseEntity<String> updateTeamName(@RequestBody UpdateTeamNameRequestDto requestDto) {
        chatService.updateTeamName(requestDto);

        return ResponseEntity.ok("팀 채팅방 이름변경 성공");
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateUserProfileDto requestDto) {
        chatService.updateUserName(requestDto);
        return ResponseEntity.ok("회원 프로필 업데이트 완료");
    }

    //팀 채팅방 리스트 조회
    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomDto>> getChatRooms(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        List<ChatRoomDto> chatRooms = chatRoomService.getChatRoomsByUserId(userId);

        return ResponseEntity.ok(chatRooms);
    }

    //채팅방 입장
    @GetMapping("/chat/room/{roomId}")
    public ResponseEntity<ChatRoomDataResponseDto> enterChatRoom(
            @PathVariable("roomId") Long roomId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);

        ChatRoomDataResponseDto chatRoomData = chatRoomService.enterChatRoom(roomId, userId);
        log.info("채팅방 입장: userId={}, roomId={}", userId, roomId);
        return ResponseEntity.ok(chatRoomData);
    }

}
