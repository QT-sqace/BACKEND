package com.example.chat_service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisInitializer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void clearRedisOnStartup() {
        try {
            // Redis에서 모든 키 삭제
            redisTemplate.delete(redisTemplate.keys("*"));
            log.info("Redis 초기화 완료: 모든 키가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Redis 초기화 중 오류 발생: " + e.getMessage());
        }
    }
}
