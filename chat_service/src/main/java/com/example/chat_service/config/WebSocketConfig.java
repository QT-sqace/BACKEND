package com.example.chat_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // Spring 설정 클래스
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커 설정
        registry.enableSimpleBroker("/sub"); // "/topic" 경로로 메시지 브로드캐스트
        registry.setApplicationDestinationPrefixes("/pub"); // 클라이언트가 메시지를 전송할 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 설정 (SockJS 지원 포함)
        registry.addEndpoint("/ws-stomp") // WebSocket 연결 엔드포인트
                .setAllowedOriginPatterns("*") // 모든 Origin 허용
                .withSockJS(); // SockJS 지원
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*");
    }
}
