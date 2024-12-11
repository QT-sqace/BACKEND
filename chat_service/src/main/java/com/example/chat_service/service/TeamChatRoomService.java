package com.example.chat_service.service;

import com.example.chat_service.dto.external.*;
import com.example.chat_service.dto.request.ChatMessageDto;
import com.example.chat_service.dto.response.ChatParticipantDto;
import com.example.chat_service.dto.response.ChatRoomDataResponseDto;
import com.example.chat_service.entity.ChatParticipant;
import com.example.chat_service.entity.ChatRoom;
import com.example.chat_service.repository.ChatParticipantRepository;
import com.example.chat_service.repository.ChatRoomRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PARTICIPANTS_KEY = "room:%d:participants";

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


    public void deleteParticipant(ChatParticipantDeleteRequestDto requestDto) {

        ChatRoom room = roomRepository.findByTeamId(requestDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당 팀의 채팅방이 존재하지 않습니다."));

        ChatParticipant participant = participantRepository.findByRoomAndUserId(room, requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저는 채팅방에 없습니다."));

        participantRepository.delete(participant);

        log.info("팀ID: {}의 참여자ID: {} 가 성공적으로 제거되었습니다.",requestDto.getTeamId(), requestDto.getUserId());
    }

    public void updateTeamName(UpdateTeamNameRequestDto requestDto) {
        ChatRoom room = roomRepository.findByTeamId(requestDto.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("해당팀의 채팅방이 없습니다."));

        room.updateTeamName(requestDto.getTeamName());

        roomRepository.save(room);
    }

    public void updateUserName(UpdateUserProfileDto requestDto) {
        Long userId = requestDto.getUserId();
        String userName = requestDto.getUserName();
        String profileImage = requestDto.getProfileImage();

        if ((userName == null || userName.isEmpty()) && (profileImage == null || profileImage.isEmpty())) {
            log.info("유저 ID {}: userName과 profileImage가 모두 비어 있습니다. 업데이트를 중단합니다.", userId);
            return;
        }

        List<ChatParticipant> participants = participantRepository.findByUserId(userId);

        if (participants.isEmpty()) {
            log.info("유저 ID {}에 대한 채팅방 데이터가 없습니다. 업데이트할 필요가 없습니다.", userId);
            return;
        }

        // MySQL에 저장된 참여자 정보 업데이트
        for (ChatParticipant participant : participants) {
            if (userName != null && !userName.isEmpty()) {
                participant.updateProfile(userName, participant.getProfileImage()); // userName만 갱신
            }
            if (profileImage != null && !profileImage.isEmpty()) {
                participant.updateProfile(participant.getUserName(), profileImage); // profileImage만 갱신
            }
        }

        participantRepository.saveAll(participants);
        log.info("유저 ID {}에 대한 프로필 정보가 업데이트되었습니다.", userId);

/*        //Redis에 참여자 정보 갱신
        for (ChatParticipant participant : participants) {
            Long roomId = participant.getRoom().getRoomId();    //참여자가 속한 roomId
            String redisKey = String.format(PARTICIPANTS_KEY, roomId);

            try {
                //redis에서 참여자 정보 가져오기
                ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                Object redisValue = valueOperations.get(redisKey);


                if (redisValue != null) {
                    String jsonValue = redisValue.toString().replace("\\\"", "\"")
                            .replace("\"[", "[").replace("]\"", "]");

                    List<ChatParticipantDto> redisParticipants = objectMapper.convertValue(
                            jsonValue,
                            new TypeReference<List<ChatParticipantDto>>() {}
                    );

                    //Redis 참여자 정보 업데이트
                    redisParticipants.forEach(redisParticipant -> {
                        if (redisParticipant.getUserId().equals(userId)) {
                            if (userName != null && !userName.isEmpty()) {
                                redisParticipant.setUserName(userName); // userName만 갱신
                            }
                            if (profileImage != null && !profileImage.isEmpty()) {
                                redisParticipant.setProfileImage(profileImage); // profileImage만 갱신
                            }
                        }
                    });

                    String updatedJson = objectMapper.writeValueAsString(redisParticipants);
                    valueOperations.set(redisKey, updatedJson);

                    log.info("Redis에서 roomId {}의 유저 ID {} 프로필이 업데이트되었습니다.", roomId, userId);
                } else {
                    log.warn("Redis에 roomId {} 참여자 정보가 없습니다.", roomId);
                }
            } catch (Exception e) {
                log.error("Redis 업데이트 중 오류 발생: {}" , e.getMessage());
            }
        }*/
    }
}
