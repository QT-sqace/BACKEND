package com.example.chat_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {
    //Redis에 메시지를 발행하는 역할

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    //Redis에 메시지를 발행
    public void publish(Object message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
