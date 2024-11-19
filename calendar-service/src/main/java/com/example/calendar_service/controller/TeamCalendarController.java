package com.example.calendar_service.controller;

import com.example.calendar_service.dto.BasicResponseDto;
import com.example.calendar_service.dto.team.TeamCalendarRequestDto;
import com.example.calendar_service.dto.team.TeamEventRequestDto;
import com.example.calendar_service.dto.user.UserCalendarRequestDto;
import com.example.calendar_service.service.TeamCalendarService;
import com.example.calendar_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamCalendarController {

    private final TeamCalendarService calendarService;
    private final JwtUtil jwtUtil;

    //팀 캘린더 생성
    @PostMapping("/create/team")
    public ResponseEntity<Void> createTeamCalendar(@RequestBody TeamCalendarRequestDto requestDto) {
        calendarService.createCalendarForTeam(requestDto.getTeamId());
        log.info("팀 캘린더 생성 완료");
        return ResponseEntity.ok().build();
    }
/*    //팀 일정 등록
    @PostMapping("/team/events")
    public ResponseEntity<BasicResponseDto> createTeamEvent(@RequestBody TeamEventRequestDto requestDto,
                                                            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        calendarService.createTeamEvent(requestDto, userId);
        return ResponseEntity.ok(BasicResponseDto.success("팀 일정 등록 완료", null));

    }*/
}
