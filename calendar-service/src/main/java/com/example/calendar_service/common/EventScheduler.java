package com.example.calendar_service.common;

import com.example.calendar_service.dto.CalendarInfoDto;
import com.example.calendar_service.entity.CalendarInfo;
import com.example.calendar_service.repository.CalendarInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventScheduler {

    private final CalendarInfoRepository calendarInfoRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    //1분 마다 실행되는 스케줄러
    @Scheduled(cron = "0 * * * * *")
    public void cacheUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        String nowString = now.format(formatter);
        String laterString = thirtyMinutesLater.format(formatter);

        log.info("Checking events between {} and {}", nowString, laterString);

        //MySQL에서 30분 이내 시작할 이벤트 조회
        List<CalendarInfo> events = calendarInfoRepository.findEventsBetween(nowString, laterString);

        for (CalendarInfo event : events) {
            CalendarInfoDto eventDto = new CalendarInfoDto(
                    event.getEventId(),
                    event.getTitle(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getColor()
            );
            String redisKey = "event:" + event.getEventId();    //Redis key 설정
            redisTemplate.opsForValue().set(redisKey, eventDto, 31, TimeUnit.MINUTES);
            log.info("Cached event: {}", eventDto.getTitle());
        }
    }
}
