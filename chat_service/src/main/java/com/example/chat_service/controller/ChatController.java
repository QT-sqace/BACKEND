package com.example.chat_service.controller;

import com.example.chat_service.dto.request.ChatMessageDto;
import com.example.chat_service.redis.RedisPublisher;
import com.example.chat_service.service.ChatMessageService;
import com.example.chat_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto messageDto) {
        log.info("WebSocket 메시지 수신: {}", messageDto);

        //1. 메시지를 Redis에 발행 (Pub/Sub 구조)
        redisPublisher.publish(messageDto);

        //2. 메시지를 mysql에 저장
        chatMessageService.handleMessage(messageDto);
        //3. 클라이언트에 메시지 브로드 캐스트 (옵션)
    }


}
