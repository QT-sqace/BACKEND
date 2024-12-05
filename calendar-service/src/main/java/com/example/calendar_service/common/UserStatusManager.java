package com.example.calendar_service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UserStatusManager {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATUS_KEY_PATTERN = "user:%d:status";

    public void setOnline(Long userId) {
        String key = String.format(STATUS_KEY_PATTERN, userId);
        redisTemplate.opsForValue().set(key, "online", 5, TimeUnit.MINUTES);
    }

    public String getStatus(Long userId) {
        String key = String.format(STATUS_KEY_PATTERN, userId);
        Object status = redisTemplate.opsForValue().get(key);
        return status == null ? "offline" : status.toString();
    }

    public boolean canUpdateStatus(Long userId) {
        String rateLimitKey = String.format("user:%d:rate_limit", userId);
        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(rateLimitKey, "1", 3, TimeUnit.MINUTES);
        return isNewRequest != null && isNewRequest;
    }
}
