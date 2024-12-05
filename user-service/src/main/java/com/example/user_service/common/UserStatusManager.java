package com.example.user_service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String STATUS_KEY_PATTERN = "user:*:status";

    public void setOnline(Long userId) {
        String key = String.format("user:%d:status", userId);
        //온라인 유지 시간은 5분으로 지정
        redisTemplate.opsForValue().set(key, "online", 5, TimeUnit.MINUTES);
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkAndUpdateStatus() {
        log.info("스프링 스케쥴 - 접속상태 갱신 실행");

        // 특정 패턴의 키 검색
        Set<String> keys = stringRedisTemplate.keys(STATUS_KEY_PATTERN);
        if (keys != null) {
            for (String key : keys) {
                // TTL 확인
                Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                if (ttl == null || ttl > 60) continue; // TTL이 없거나 1분 이상 남았으면 넘어감

                String currentStatus = (String) redisTemplate.opsForValue().get(key);
                if (currentStatus == null) continue; // 키가 이미 삭제된 경우

                // 상태 변경 로직
                if ("online".equals(currentStatus)) {
                    // 'online' 상태 -> 'away'로 변경하고 TTL을 5분으로 설정
                    redisTemplate.opsForValue().set(key, "away", 5, TimeUnit.MINUTES);
                    log.info("Updated {} to 'away' with new TTL 10 minutes", key);
                } else if ("away".equals(currentStatus)) {
                    // 'away' 상태 -> TTL 만료 시 키 삭제
                    if (ttl <= 0) {
                        redisTemplate.delete(key);
                        log.info("Deleted {} as it is now 'offline'", key);
                    }
                }
            }
        }
    }

    public String getStatus(Long userId) {
        String key = String.format("user:%d:status", userId);
        Object status = redisTemplate.opsForValue().get(key);
        return status == null ? "offline" : status.toString();
    }

    public boolean canUpdateStatus(Long userId) {
        String rateLimitKey = String.format("user:%d:rate_limit", userId);
        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(rateLimitKey, "1", 3, TimeUnit.MINUTES);
        return isNewRequest != null && isNewRequest;
    }
}
