package com.example.team_service.client;

import com.example.team_service.dto.external.TeamCalendarRequestDto;
import com.example.team_service.dto.response.CalendarInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "calendar-service-client", url = "http://calendar-service.spring-boot-app.svc.cluster.local:8084")
public interface CalendarServiceClient {

    //팀 생성 시점에 캘린더 서비스로 캘린더 생성요청 클라이언트
    @PostMapping("/create/team")
    void createTeamCalendar(@RequestBody TeamCalendarRequestDto requestDto);

    //팀 일정 동기화 요청
    @PostMapping("/sync/{teamId}/{userId}")
    void syncTeamEventsToPersonalCalendar(@PathVariable("teamId") Long teamId, @PathVariable("userId") Long userId);

    @GetMapping("/team/calendar/{teamId}")
    List<CalendarInfoDto> getTeamEvents(@PathVariable("teamId") Long teamId);
}
