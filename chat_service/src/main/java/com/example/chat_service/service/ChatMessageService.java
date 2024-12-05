package com.example.chat_service.service;

import com.example.chat_service.dto.request.ChatMessageDto;
import com.example.chat_service.dto.response.ChatRoomDto;
import com.example.chat_service.entity.ChatMessage;
import com.example.chat_service.entity.ChatParticipant;
import com.example.chat_service.entity.ChatRoom;
import com.example.chat_service.repository.ChatMessageRepository;
import com.example.chat_service.repository.ChatParticipantRepository;
import com.example.chat_service.repository.ChatRoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository participantRepository;

    private static final String CHATROOM_MESSAGES_CACHE_KEY = "chatroom:%d:messages";
    private static final String LAST_MESSAGE_CACHE_KEY = "room:%d:lastMessage";
    private static final String UNREAD_COUNT_CACHE_KEY = "room:%d:user:%d:unread";
    private static final String CHATROOM_LIST_CACHE_KEY = "user:%s:chatrooms";
    private static final int RECENT_MESSAGE_COUNT = 100;

    /**
     * 메시지 처리 및 저장
     */
    public void handleMessage(ChatMessageDto messageDto) {
        log.info("새 메시지 처리 시작: {}", messageDto);
        // MySQL에 메시지 저장
        ChatMessage savedMessage = saveMessageToMySQL(messageDto);

        // Redis에 저장된 메시지 목록과 마지막 메시지 갱신
        saveMessageToRedis(savedMessage);
        updateLastMessageCache(savedMessage);

        // 읽지 않은 메시지 수 갱신
        updateUnreadCountForParticipants(savedMessage);

        // 전송자의 lastReadMessageId 갱신
        updateLastReadMessageId(savedMessage);

        //Redis의 CHATROOM_LIST_CACHE_KEY 갱신
        updateChatRoomsInRedis(savedMessage);
        // 로그 추가
        log.info("메시지 처리 완료: roomId={}, messageId={}, content={}",
                savedMessage.getRoom().getRoomId(),
                savedMessage.getMessageId(),
                extractContentFromJson(savedMessage.getContent()));
    }

    /**
     * MySQL에 메시지 저장
     */
    private ChatMessage saveMessageToMySQL(ChatMessageDto messageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 마지막 roomMessageId 조회
        Long lastRoomMessageId = chatMessageRepository.findMaxRoomMessageIdByRoomId(chatRoom.getRoomId()).orElse(0L);
        Long newRoomMessageId = lastRoomMessageId + 1;

        // 새 메시지 생성
        ChatMessage chatMessage = new ChatMessage(
                chatRoom,
                newRoomMessageId, // roomMessageId 설정
                messageDto.getSenderId(),
                convertContentToJson(messageDto.getContent(), messageDto.getMessageType()),
                LocalDateTime.now(),
                messageDto.getMessageType()
        );

        // 메시지 저장
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        log.info("MySQL에 메시지 저장 완료: roomId={}, roomMessageId={}, messageId={}",
                chatMessage.getRoom().getRoomId(),
                chatMessage.getRoomMessageId(),
                chatMessage.getMessageId());

        return savedMessage;
    }


    /**
     * Redis에 메시지 저장 (최근 100개 유지)
     */
    private void saveMessageToRedis(ChatMessage savedMessage) {
        String redisKey = String.format(CHATROOM_MESSAGES_CACHE_KEY, savedMessage.getRoom().getRoomId());

        // content에서 text 값 추출
        String textContent = extractContentFromJson(savedMessage.getContent());

        // 메시지 내용을 Map으로 변환하여 저장
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageId", savedMessage.getMessageId());
        messageMap.put("roomId", savedMessage.getRoom().getRoomId());
        messageMap.put("senderId", savedMessage.getSenderId());
        messageMap.put("content", textContent); // 추출한 text 값만 저장
        messageMap.put("sendTime", savedMessage.getSendTime().toString()); // LocalDateTime -> String 변환
        messageMap.put("messageType", savedMessage.getMessageType());

        // Redis에 저장
        redisTemplate.opsForList().rightPush(redisKey, messageMap);

        // 최근 메시지 개수 유지
        if (redisTemplate.opsForList().size(redisKey) > RECENT_MESSAGE_COUNT) {
            redisTemplate.opsForList().leftPop(redisKey);
        }

        log.info("Redis에 메시지 추가: roomId={}, messageId={}, content={}", savedMessage.getRoom().getRoomId(), savedMessage.getMessageId(), textContent);
    }



    /**
     * Redis 캐시에 마지막 메시지 갱신
     */
    private void updateLastMessageCache(ChatMessage savedMessage) {
        String lastMessageKey = String.format(LAST_MESSAGE_CACHE_KEY, savedMessage.getRoom().getRoomId());
        String content = extractContentFromJson(savedMessage.getContent());

        // Redis에 직렬화 하지 않고 JSON 문자열 그대로 저장
        redisTemplate.opsForValue().set(lastMessageKey, content, 10, TimeUnit.MINUTES);

        log.info("Redis 캐시 갱신: roomId={}, lastMessage={}", savedMessage.getRoom().getRoomId(), content);
    }

    /**
     * 참여자들의 unreadCount 갱신
     */
    private void updateUnreadCountForParticipants(ChatMessage savedMessage) {
        Long roomId = savedMessage.getRoom().getRoomId();
        Long senderId = savedMessage.getSenderId();

        // 현재 채팅방에 접속해 있는 사용자 목록 가져오기
        Set<Long> usersInRoom = getUsersInRoom(roomId);

        List<ChatParticipant> participants = participantRepository.findByRoom(savedMessage.getRoom());

        for (ChatParticipant participant : participants) {
            Long userId = participant.getUserId();
            String unreadKey = String.format(UNREAD_COUNT_CACHE_KEY, roomId, userId);

            if (!userId.equals(senderId) && !usersInRoom.contains(userId)) {
                // 메시지 보낸 사용자나 채팅방에 접속한 사용자가 아닌 경우 unreadCount 증가
                String cachedUnreadCountStr = (String) redisTemplate.opsForValue().get(unreadKey);
                Long unreadCount = (cachedUnreadCountStr != null) ? Long.valueOf(cachedUnreadCountStr) : 0L;

                unreadCount++;
                redisTemplate.opsForValue().set(unreadKey, unreadCount.toString(), 10, TimeUnit.MINUTES);
            } else {
                // 메시지 보낸 사용자와 채팅방에 접속한 사용자는 unreadCount를 0으로 설정
                redisTemplate.opsForValue().set(unreadKey, "0", 10, TimeUnit.MINUTES);
            }
        }
    }


    /**
     * Redis에서 현재 채팅방에 접속한 사용자 목록 가져오기
     */
    private Set<Long> getUsersInRoom(Long roomId) {
        String key = String.format("room:%d:users", roomId);
        Set<Object> users = redisTemplate.opsForSet().members(key);

        // Object -> Long 변환
        if (users != null) {
            return users.stream()
                    .map(user -> Long.valueOf(user.toString()))
                    .collect(Collectors.toSet());
        }

        return new HashSet<>();
    }


    /**
     * 전송자의 lastReadMessageId 갱신
     */
    private void updateLastReadMessageId(ChatMessage savedMessage) {
        ChatParticipant participant = participantRepository.findByRoomAndUserId(savedMessage.getRoom(), savedMessage.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 채팅방에 참여하지 않았습니다."));
        participant.setLastReadMessageId(savedMessage.getMessageId());
        participantRepository.save(participant);
    }

    /**
     * 메시지 내용을 JSON으로 변환
     */
    private String convertContentToJson(String content, ChatMessage.MessageType messageType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("type", messageType.name());
            messageMap.put("text", content);
            return objectMapper.writeValueAsString(messageMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }

    /**
     *메시지가 갱신되면 웹소켓 연결전에도 마지막 도착 메시지 갱신
     */

    private void updateChatRoomsInRedis(ChatMessage savedMessage) {
        List<ChatParticipant> participants = participantRepository.findByRoom(savedMessage.getRoom());

        for (ChatParticipant participant : participants) {
            String cacheKey = String.format(CHATROOM_LIST_CACHE_KEY, participant.getUserId());

            // Redis에서 기존 채팅방 목록을 조회
            String cachedChatRoomsJson = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cachedChatRoomsJson != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<ChatRoomDto> chatRoomDtos = objectMapper.readValue(
                            cachedChatRoomsJson,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, ChatRoomDto.class)
                    );

                    // 해당 채팅방의 lastMessage를 업데이트
                    for (ChatRoomDto chatRoomDto : chatRoomDtos) {
                        if (chatRoomDto.getRoomId().equals(savedMessage.getRoom().getRoomId())) {
                            chatRoomDto.setLastMessage(extractContentFromJson(savedMessage.getContent()));
                            break;
                        }
                    }

                    // 업데이트된 목록을 Redis에 다시 저장
                    String updatedJson = objectMapper.writeValueAsString(chatRoomDtos);
                    redisTemplate.opsForValue().set(cacheKey, updatedJson, 10, TimeUnit.MINUTES);

                    log.info("Redis의 채팅방 목록 업데이트 완료: userId={}, roomId={}, lastMessage={}",
                            participant.getUserId(),
                            savedMessage.getRoom().getRoomId(),
                            extractContentFromJson(savedMessage.getContent()));

                } catch (Exception e) {
                    log.error("Redis의 채팅방 목록 업데이트 중 오류 발생: {}", e.getMessage());
                }
            }
        }
    }


    /**
     * JSON 데이터에서 텍스트 추출
     */
    private String extractContentFromJson(String jsonContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageMap = objectMapper.readValue(jsonContent, Map.class);
            return messageMap.get("text");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }
}

