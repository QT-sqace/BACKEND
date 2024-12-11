package com.example.chat_service.service;

import com.example.chat_service.dto.request.ChatMessageDto;
import com.example.chat_service.dto.response.ChatParticipantDto;
import com.example.chat_service.dto.response.ChatRoomDataResponseDto;
import com.example.chat_service.dto.response.ChatRoomDto;
import com.example.chat_service.entity.ChatMessage;
import com.example.chat_service.entity.ChatParticipant;
import com.example.chat_service.entity.ChatRoom;
import com.example.chat_service.repository.ChatMessageRepository;
import com.example.chat_service.repository.ChatParticipantRepository;
import com.example.chat_service.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository roomRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CHATROOM_LIST_CACHE_KEY = "user:%s:chatrooms";
    private static final String UNREAD_COUNT_CACHE_KEY = "room:%d:user:%d:unread";
    private static final String LAST_MESSAGE_CACHE_KEY = "room:%d:lastMessage";
    private static final String PARTICIPANTS_KEY = "room:%d:participants";
    private static final String MESSAGES_KEY = "chatroom:%d:messages";

    /**
     * 채팅방 조회 (캐시 어사이드)
     */
    public List<ChatRoomDto> getChatRoomsByUserId(Long userId) {
        String cacheKey = String.format(CHATROOM_LIST_CACHE_KEY, userId);

        // Redis에서 캐시 조회
        String cachedChatRoomsJson = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cachedChatRoomsJson != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<ChatRoomDto> cachedChatRooms = objectMapper.readValue(
                        cachedChatRoomsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ChatRoomDto.class)
                );
                log.info("Redis에서 채팅방 조회 성공: {}", cachedChatRooms);

                if (cachedChatRooms.isEmpty()) {
                    throw new IllegalArgumentException("사용자가 참여중인 채팅방이 없습니다.");
                }

                // **변경된 부분: Redis에서 unreadCount 값을 읽어서 갱신**
                for (ChatRoomDto chatRoom : cachedChatRooms) {
                    String unreadKey = String.format(UNREAD_COUNT_CACHE_KEY, chatRoom.getRoomId(), userId);
                    Integer cachedUnreadCount = (Integer) redisTemplate.opsForValue().get(unreadKey);
                    chatRoom.setUnreadCount((long) (cachedUnreadCount != null ? cachedUnreadCount : 0));
                }

                return cachedChatRooms;
            } catch (Exception e) {
                log.error("Redis에서 채팅방 조회 중 오류 발생: {}", e.getMessage());
            }
        }

        // Redis에 캐시가 없다면 MySQL에서 조회
        List<ChatParticipant> participants = participantRepository.findByUserId(userId);
        List<ChatRoomDto> chatRoomDtos = participants.stream()
                .map(participant -> {
                    ChatRoom room = participant.getRoom();

                    // 마지막 메시지 조회 (Redis 먼저, 없으면 MySQL에서 조회)
                    String lastMessageContent = (String) redisTemplate.opsForValue().get(
                            String.format(LAST_MESSAGE_CACHE_KEY, room.getRoomId())
                    );
                    if (lastMessageContent == null) {
                        ChatMessage lastMessage = messageRepository.findTopByRoomOrderBySendTimeDesc(room);
                        lastMessageContent = lastMessage != null ? extractTextFromContent(lastMessage.getContent()) : "메시지가 없습니다.";

                        // Redis 캐시에 갱신
                        redisTemplate.opsForValue().set(
                                String.format(LAST_MESSAGE_CACHE_KEY, room.getRoomId()),
                                lastMessageContent,
                                10,
                                TimeUnit.MINUTES
                        );
                    }

                    // 읽지 않은 메시지 수 계산
                    Long unreadCount = getUnreadCount(participant, room);

                    // 채팅방 이미지 설정
                    String roomImage = room.getRoomState() == ChatRoom.RoomState.TEAM
                            ? room.getRoomImage()
                            : participant.getProfileImage();

                    return new ChatRoomDto(
                            room.getRoomId(),
                            room.getRoomName(),
                            lastMessageContent,
                            unreadCount,
                            roomImage
                    );
                })
                .collect(Collectors.toList());

        if (chatRoomDtos.isEmpty()) {
            throw new IllegalArgumentException("사용자가 참여중인 채팅방이 없습니다.");
        }

        // Redis에 캐싱
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(chatRoomDtos);
            redisTemplate.opsForValue().set(cacheKey, jsonValue, 10, TimeUnit.MINUTES);
            log.info("Redis에 채팅방 목록 캐싱: {}", jsonValue);
        } catch (Exception e) {
            log.error("Redis에 채팅방 목록 캐싱 중 오류 발생: {}", e.getMessage());
        }

        return chatRoomDtos;
    }

    /**
     * 팀 가입 시점에 redis 갱신
     */
    public void updateUserChatRoomsInRedis(Long userId) {
        String cacheKey = String.format(CHATROOM_LIST_CACHE_KEY, userId);

        // MySQL에서 사용자의 모든 채팅방 가져오기
        List<ChatParticipant> participants = participantRepository.findByUserId(userId);
        List<ChatRoomDto> chatRoomDtos = participants.stream().map(participant -> {
            ChatRoom room = participant.getRoom();

            // 마지막 메시지 조회
            ChatMessage lastMessage = messageRepository.findTopByRoomOrderBySendTimeDesc(room);
            String lastMessageContent = lastMessage != null ? extractTextFromContent(lastMessage.getContent()) : "메시지가 없습니다.";

            // 읽지 않은 메시지 수 계산
            Long unreadCount = getUnreadCount(participant, room);

            // 채팅방 이미지 설정
            String roomImage = room.getRoomState() == ChatRoom.RoomState.TEAM
                    ? room.getRoomImage()
                    : participant.getProfileImage();

            return new ChatRoomDto(
                    room.getRoomId(),
                    room.getRoomName(),
                    lastMessageContent,
                    unreadCount,
                    roomImage
            );
        }).collect(Collectors.toList());

        // Redis에 갱신
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(chatRoomDtos);
            redisTemplate.opsForValue().set(cacheKey, jsonValue, 10, TimeUnit.MINUTES);
            log.info("Redis 갱신 완료: userId = {}, 채팅방 수: {}", userId, chatRoomDtos.size());
        } catch (Exception e) {
            log.error("Redis 갱신 중 오류 발생: {}", e.getMessage());
        }
    }


    /**
     * 읽지 않은 메시지 수 계산
     */
/*    private Long getUnreadCount(ChatParticipant participant, ChatRoom room) {
        String unreadKey = String.format(UNREAD_COUNT_CACHE_KEY, room.getRoomId(), participant.getUserId());

        // Redis에서 unreadCount 조회
        String cachedUnreadCountStr = (String) redisTemplate.opsForValue().get(unreadKey);
        log.info("Unread count from Redis: key={}, value={}", unreadKey, cachedUnreadCountStr);

        // String -> Long 변환 (기본값 0 설정)
        Long cachedUnreadCount = (cachedUnreadCountStr != null) ? Long.valueOf(cachedUnreadCountStr) : 0L;

        // Redis에 데이터가 없으면 MySQL로 계산
        if (cachedUnreadCount == 0) {
            Long lastReadMessageId = participant.getLastReadMessageId() != null ? participant.getLastReadMessageId() : 0L;
            Long unreadCount = messageRepository.countByRoomAndMessageIdGreaterThan(room, lastReadMessageId);

            // Redis에 캐싱
            redisTemplate.opsForValue().set(unreadKey, unreadCount.toString(), 10, TimeUnit.MINUTES);
            log.info("Unread count calculated from MySQL: userId={}, roomId={}, count={}", participant.getUserId(), room.getRoomId(), unreadCount);

            return unreadCount;
        }

        return cachedUnreadCount;
    }*/



    /**
     * 메시지 컨텐츠에서 텍스트 추출
     */
    private String extractTextFromContent(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageMap = objectMapper.readValue(content, Map.class);
            return messageMap.get("text");
        } catch (Exception e) {
            log.error("메시지 컨텐츠 파싱 실패: {}", e.getMessage());
            return "메시지가 없습니다.";
        }
    }


    /**
     * 채팅방 입장
     */
    public ChatRoomDataResponseDto enterChatRoom(Long roomId, Long userId) {
        List<ChatParticipantDto> participants = loadParticipantsFromDB(roomId);

        cacheParticipantsToRedis(roomId, participants);
/*        //기존 코드

        // Redis에서 참여자 정보 조회
        List<ChatParticipantDto> participants = getParticipantsFromRedis(roomId);

        // Redis에 참여자 정보가 없으면 DB에서 조회 후 Redis에 캐시
        if (participants.isEmpty()) {
            participants = loadParticipantsFromDB(roomId);
            cacheParticipantsToRedis(roomId, participants);
        }*/

        // 현재 사용자 추가 (참여자 목록 갱신)
        boolean isUserInParticipants = participants.stream()
                .anyMatch(participant -> participant.getUserId().equals(userId));

        if (!isUserInParticipants) {
            ChatParticipantDto newParticipant = loadParticipantFromDB(roomId, userId);
            if (newParticipant != null) {
                participants.add(newParticipant);
                cacheParticipantsToRedis(roomId, participants); // Redis 갱신
            }
        }

        // Redis에서 이전 메시지 조회
        List<ChatMessageDto> messages = getMessagesFromRedis(roomId);

        // Redis에 메시지가 없으면 DB에서 조회 후 캐싱
        if (messages.isEmpty()) {
            messages = loadMessagesFromDB(roomId);
            cacheMessagesToRedis(roomId, messages);
        }

        // 메시지가 없으면 기본값 처리 후 반환
        if (messages.isEmpty()) {
            log.warn("No messages found for roomId: {}, userId: {}", roomId, userId);
            return new ChatRoomDataResponseDto(List.of(), participants);
        }

        // lastReadMessageId 갱신
        updateLastReadMessageId(userId, roomId);

        return new ChatRoomDataResponseDto(messages, participants);
    }


    private ChatParticipantDto loadParticipantFromDB(Long roomId, Long userId) {
        Optional<ChatParticipant> participantOptional = participantRepository.findByRoomAndUserId(
                roomRepository.findById(roomId).orElseThrow(() ->
                        new IllegalArgumentException("채팅방이 존재하지 않습니다.")),
                userId
        );

        if (participantOptional.isPresent()) {
            ChatParticipant participant = participantOptional.get();
            return new ChatParticipantDto(
                    participant.getUserId(),
                    participant.getUserName(),
                    participant.getProfileImage()
            );
        }
        return null;
    }




    /**
     * Redis에서 참여자 정보 조회
     */
    private List<ChatParticipantDto> getParticipantsFromRedis(Long roomId) {
        String key = String.format(PARTICIPANTS_KEY, roomId);

        try {
            String jsonValue = (String) redisTemplate.opsForValue().get(key); // JSON 문자열 조회
            if (jsonValue == null) {
                return List.of(); // 데이터가 없을 경우 빈 리스트 반환
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    jsonValue,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ChatParticipantDto.class)
            ); // JSON 문자열을 리스트로 역직렬화
        } catch (Exception e) {
            log.error("Redis에서 참여자 정보 조회 중 오류 발생: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * DB에서 참여자 정보 조회
     */
    private List<ChatParticipantDto> loadParticipantsFromDB(Long roomId) {
        List<ChatParticipant> participants = participantRepository.findByRoomId(roomId);

        return participants.stream()
                .map(participant -> new ChatParticipantDto(
                        participant.getUserId(),
                        participant.getUserName(),
                        participant.getProfileImage()
                ))
                .toList();
    }
    /**
     * Redis에 참여자 정보 캐싱
     */
    private void cacheParticipantsToRedis(Long roomId, List<ChatParticipantDto> participants) {
        String key = String.format(PARTICIPANTS_KEY, roomId);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(participants);
            redisTemplate.opsForValue().set(key, jsonValue, 10, TimeUnit.MINUTES);
            log.info("Cached participants for room {}: {}", roomId, jsonValue);
        } catch (Exception e) {
            log.error("Failed to cache participants for room {}: {}", roomId, e.getMessage());
        }
    }


    /**
     * Redis에서 메시지 조회
     */
    private List<ChatMessageDto> getMessagesFromRedis(Long roomId) {
        String key = String.format(MESSAGES_KEY, roomId);
        List<Object> cachedMessages = redisTemplate.opsForList().range(key, 0, -1);

        if (cachedMessages == null || cachedMessages.isEmpty()) {
            return List.of(); // 캐시 데이터가 없을 경우 빈 리스트 반환
        }

        // 참여자 정보 조회
        String participantsKey = String.format(PARTICIPANTS_KEY, roomId);
        String participantsJson = (String) redisTemplate.opsForValue().get(participantsKey);

        List<ChatParticipantDto> participants; // 초기화하지 않고 선언만
        if (participantsJson != null) {
            participants = parseParticipantsJson(participantsJson);
        } else {
            participants = List.of(); // JSON 데이터가 없을 경우 빈 리스트로 설정
        }

        // 메시지를 DTO로 변환하며 senderName과 senderProfileImage 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTimeModule 자동 등록

        return cachedMessages.stream()
                .map(obj -> {
                    ChatMessageDto message = objectMapper.convertValue(obj, ChatMessageDto.class);

                    // senderId로 참여자 정보 조회
                    ChatParticipantDto participant = participants.stream()
                            .filter(p -> p.getUserId().equals(message.getSenderId()))
                            .findFirst()
                            .orElse(new ChatParticipantDto(null, "Unknown User",
                                    "http://172.16.211.103:9000/profile/defaultImage.png"));

                    message.setSenderName(participant.getUserName());
                    message.setSenderProfileImage(participant.getProfileImage());

                    return message;
                })
                .toList();
    }

    private List<ChatParticipantDto> parseParticipantsJson(String participantsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    participantsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ChatParticipantDto.class)
            );
        } catch (Exception e) {
            log.error("Failed to parse participants from Redis: {}", e.getMessage());
            return List.of(); // 파싱 실패 시 빈 리스트 반환
        }
    }

    private List<ChatParticipantDto> initializeParticipants(String participantsJson) {
        if (participantsJson != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(
                        participantsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ChatParticipantDto.class)
                );
            } catch (Exception e) {
                log.error("Failed to parse participants from Redis: {}", e.getMessage());
            }
        }
        return List.of(); // 실패 시 빈 리스트 반환
    }
    /**
     * Redis에 메시지 캐싱
     */
    private void cacheMessagesToRedis(Long roomId, List<ChatMessageDto> messages) {
        String key = String.format(MESSAGES_KEY, roomId);
        log.info("Caching messages to Redis: {}", messages);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        messages.forEach(message -> {
            Object sendTime = message.getSendTime(); // 타입 확인용 변수
            if (sendTime instanceof LocalDateTime) {
                // LocalDateTime -> String 변환
                message.setSendTime(((LocalDateTime) sendTime).format(formatter));
            } else if (sendTime instanceof String) {
                // 이미 String인 경우 처리 불필요
                log.debug("sendTime is already a String: {}", sendTime);
            } else {
                log.warn("Unexpected type for sendTime: {}", sendTime);
            }
            redisTemplate.opsForList().rightPush(key, message);
        });

        redisTemplate.expire(key, 10, TimeUnit.MINUTES); // 캐시 만료 시간 설정
    }

    /**
     * DB에서 메시지 조회
     */
    private List<ChatMessageDto> loadMessagesFromDB(Long roomId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<ChatMessage> messages = messageRepository.findTop50ByRoomRoomIdOrderBySendTimeDesc(roomId);

        return messages.stream()
                .map(message -> new ChatMessageDto(
                        message.getRoom().getRoomId(),
                        message.getSenderId(),
                        getSenderName(message.getSenderId()),
                        getSenderProfileImage(message.getSenderId()),
                        extractTextFromContent(message.getContent()),
                        message.getMessageType(),
                        message.getSendTime().format(formatter) // LocalDateTime을 String으로 변환
                ))
                .toList();
    }



    private String getSenderName(Long senderId) {
        String senderName = (String) redisTemplate.opsForHash().get("user:" + senderId, "name");
        if (senderName == null) {
            log.warn("Redis에서 senderName을 찾지 못했습니다. senderId: {}", senderId);
            return "Unknown User"; // 기본값 설정
        }
        return senderName;
    }

    private String getSenderProfileImage(Long senderId) {
        String profileImage = (String) redisTemplate.opsForHash().get("user:" + senderId, "profileImage");
        if (profileImage == null) {
            log.warn("Redis에서 profileImage를 찾지 못했습니다. senderId: {}", senderId);
            return "/images/user/defaultImage.png"; // 기본값 설정
        }
        return profileImage;
    }

    /**
     * lastReadMessageId 갱신
     */
    private void updateLastReadMessageId(Long userId, Long roomId) {
        // 현재 방에서 가장 최신 메시지 ID 조회
        Long lastMessageRoomMessageId = messageRepository.findMaxRoomMessageIdByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("메시지가 존재하지 않습니다."));

        // ChatParticipant 업데이트
        ChatParticipant participant = participantRepository.findByRoomAndUserId(
                roomRepository.findById(roomId).orElseThrow(() ->
                        new IllegalArgumentException("채팅방이 존재하지 않습니다.")),
                userId
        ).orElseThrow(() -> new IllegalArgumentException("사용자가 채팅방에 참여하지 않았습니다."));

        participant.setLastReadMessageId(lastMessageRoomMessageId);
        participantRepository.save(participant);

        // Redis에 갱신
        String key = String.format("room:%d:user:%d:unread", roomId, userId);
        redisTemplate.opsForValue().set(key, "0", 10, TimeUnit.MINUTES); // 읽음 처리
        log.info("Updated lastReadMessageId: roomId={}, userId={}, lastReadMessageId={}", roomId, userId, lastMessageRoomMessageId);
    }





    /**
     * 읽지 않은 메시지 수 계산
     */
    private Long getUnreadCount(ChatParticipant participant, ChatRoom room) {
        Long lastReadMessageId = participant.getLastReadMessageId() != null ? participant.getLastReadMessageId() : 0L;

        // 현재 방에서 가장 최신 메시지 ID 가져오기
        Long latestMessageId = messageRepository.findMaxRoomMessageIdByRoomId(room.getRoomId()).orElse(0L);

        // 최신 메시지 ID와 마지막으로 읽은 메시지 ID를 비교하여 차이 계산
        Long unreadCount = latestMessageId > lastReadMessageId ? latestMessageId - lastReadMessageId : 0L;

        // Redis에 캐싱
        String unreadKey = String.format("room:%d:user:%d:unread", room.getRoomId(), participant.getUserId());
        redisTemplate.opsForValue().set(unreadKey, unreadCount.toString(), 10, TimeUnit.MINUTES);
        log.info("Unread count calculated: roomId={}, userId={}, unreadCount={}", room.getRoomId(), participant.getUserId(), unreadCount);

        return unreadCount;
    }
}
