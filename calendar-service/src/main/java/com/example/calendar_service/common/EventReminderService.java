package com.example.calendar_service.common;

import com.example.calendar_service.dto.CalendarInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventReminderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 객체 생성

    @Scheduled(cron = "0 * * * * *")
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        Set<String> keys = redisTemplate.keys("event:*");

        if (keys != null) {
            for (String key : keys) {
                try {
                    Object redisData = redisTemplate.opsForValue().getAndDelete(key);
                    if (redisData != null) {
                        CalendarInfoDto event = objectMapper.convertValue(redisData, CalendarInfoDto.class);

                        if (isThirtyMinutesBefore(event.getStartDate(), now)) {
                            sendEmail(event);
                            log.info("일정 메일 보내기 완료: {}", event.getTitle());
                        }
                    } else {
                        log.info("이미 삭제된 키: {}", key);
                    }
                } catch (Exception e) {
                    log.error("Error processing event with key: " + key, e);
                }
            }
        }
    }


    private boolean isThirtyMinutesBefore(String startDate, LocalDateTime now) {
        LocalDateTime eventStart = LocalDateTime.parse(startDate);
        return now.isAfter(eventStart.minusMinutes(30)) && now.isBefore(eventStart);
    }

    private void sendEmail(CalendarInfoDto event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("gownsqkqh03@naver.com");
        message.setSubject("일정 알림: " + event.getTitle());
        message.setText("30분 후에 일정이 시작됩니다!\n" +
                "제목: " + event.getTitle() + "\n" +
                "시작 시간: " + event.getStartDate());
        mailSender.send(message);
    }
}
