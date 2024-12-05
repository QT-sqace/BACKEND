package com.example.chat_service.redis;

import com.example.chat_service.dto.request.ChatMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 발행된 메시지를 수신하고 WebSocket으로 브로드캐스트
     */
    public void sendMessage(String publishMessage) {
        try {
            ChatMessageDto messageDto = objectMapper.readValue(publishMessage, ChatMessageDto.class);
            messagingTemplate.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
        } catch (Exception e) {
            log.error("RedisSubscriber Exception: {}", e.getMessage());
        }
    }
}
