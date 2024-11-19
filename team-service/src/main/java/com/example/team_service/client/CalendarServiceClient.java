package com.example.team_service.client;

import com.example.team_service.dto.external.TeamCalendarRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "calendar-service-client", url = "http://localhost:8000/calendarservice")
public interface CalendarServiceClient {

    //팀 생성 시점에 캘린더 서비스로 캘린더 생성요청 클라이언트
    @PostMapping("/create/team")
    void createTeamCalendar(@RequestBody TeamCalendarRequestDto requestDto);
}
