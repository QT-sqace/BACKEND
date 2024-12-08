package com.example.chat_service.service;

import com.example.chat_service.dto.external.ChatParticipantAddRequestDto;
import com.example.chat_service.dto.external.TeamChatRequestDto;
import com.example.chat_service.dto.request.ChatMessageDto;
import com.example.chat_service.dto.response.ChatParticipantDto;
import com.example.chat_service.dto.response.ChatRoomDataResponseDto;
import com.example.chat_service.entity.ChatParticipant;
import com.example.chat_service.entity.ChatRoom;
import com.example.chat_service.repository.ChatParticipantRepository;
import com.example.chat_service.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamChatRoomService {
    //팀 생성시 자동 팀 채팅방 생성과 팀원 가입시 자동 채팅방 입장

    private final ChatRoomRepository roomRepository;
    private final ChatParticipantRepository participantRepository;

    //채팅방 생성
    public void createRoom(TeamChatRequestDto requestDto) {
        ChatRoom room = new ChatRoom(requestDto.getTeamId(),
                requestDto.getRoomName(), requestDto.getRoomImage(), ChatRoom.RoomState.TEAM);

        log.info("유저 닉네임값 확인: {}", requestDto.getUserName());
        log.info("시간 확인: " +room.getRoomCreatedAT());
        roomRepository.save(room);

        LocalDateTime joinDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        ChatParticipant participant = new ChatParticipant(room,
                requestDto.getCreatorUserId(),
                requestDto.getUserName(),
                requestDto.getCreatorProfileImage(),
                joinDate);
        participantRepository.save(participant);
        log.info("유저 닉네임 받아오는거 확인: {}", participant.getUserName());
    }

    //채팅방에 팀원 추가
    public void addParticipant(ChatParticipantAddRequestDto requestDto) {
        ChatRoom room = roomRepository.findByTeamId(requestDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당 팀의 채팅방이 존재하지 않습니다."));

        ChatParticipant participant = new ChatParticipant(
                room,
                requestDto.getUserId(),
                requestDto.getUserName(),
                requestDto.getProfileImage(),
                requestDto.getJoinDate());
        participantRepository.save(participant);
        log.info("페인으로 받을때 유저 닉네임 확인: {}", participant.getUserName());
        log.info("팀 채팅방에 추가 userId: {} 채팅방: {}", requestDto.getUserId(), requestDto.getTeamId());
    }


}
