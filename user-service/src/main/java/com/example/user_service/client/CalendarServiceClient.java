package com.example.user_service.client;

import com.example.user_service.dto.external.UserCalendarRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "calendar-service-client", url = "http://gateway.spring-boot-app.svc.cluster.local/calendarservice")
public interface CalendarServiceClient {

    @PostMapping("/create/personal")
    void createPersonalCalendar(@RequestBody UserCalendarRequestDto requestDto);
}
