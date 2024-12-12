package com.example.calendar_service.controller;

import com.example.calendar_service.dto.BasicResponseDto;
import com.example.calendar_service.dto.external.TeamCalendarRequestDto;
import com.example.calendar_service.dto.team.TeamEventRequestDto;
import com.example.calendar_service.dto.team.TeamEventResponseDto;
import com.example.calendar_service.dto.team.TeamEventUpdateRequestDto;
import com.example.calendar_service.service.TeamCalendarService;
import com.example.calendar_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TeamCalendarController {

    private final TeamCalendarService calendarService;
    private final JwtUtil jwtUtil;

    //팀 캘린더 생성 - feign
    @PostMapping("/create/team")
    public ResponseEntity<Void> createTeamCalendar(@RequestBody TeamCalendarRequestDto requestDto) {
        calendarService.createCalendarForTeam(requestDto.getTeamId());
        log.info("팀 캘린더 생성 완료");
        return ResponseEntity.ok().build();
    }

    //팀 나중 가입시 일정가져오기 - feign
    @PostMapping("/sync/{teamId}/{userId}")
    public ResponseEntity<BasicResponseDto> syncTeamEventsToPersonalCalendar(
            @PathVariable("teamId") Long teamId, @PathVariable("userId") Long userId) {
        calendarService.syncTeamEventsToPersonalCalendar(teamId, userId);
        return ResponseEntity.ok(BasicResponseDto.success("팀 일정 동기화 완료", null));
    }

    //팀 메인화면 캘린더 정보 반환 - feign
    @GetMapping("/team/calendar/{teamId}")
    public ResponseEntity<List<TeamEventResponseDto>> getTeamCalendar(@PathVariable("teamId") Long teamId) {
        List<TeamEventResponseDto> events = calendarService.getTeamEvents(teamId);
        return ResponseEntity.ok(events);
    }

    //팀 일정 조회
    @GetMapping("/team/events/{teamId}")
    public ResponseEntity<BasicResponseDto> getTeamEvent(@PathVariable Long teamId,
                                                         @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);

        //팀 멤버인지 확인
        calendarService.verifyTeamMember(teamId, userId);
        //팀 일정 조회
        List<TeamEventResponseDto> events = calendarService.getTeamEvents(teamId);
        return ResponseEntity.ok(BasicResponseDto.success("팀 일정 조회 완료", events));
    }

    //팀 일정 등록
    @PostMapping("/team/events")
    public ResponseEntity<BasicResponseDto> createTeamEvent(@RequestBody TeamEventRequestDto requestDto,
                                                            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        calendarService.createTeamEvent(requestDto, userId);
        return ResponseEntity.ok(BasicResponseDto.success("팀 일정 등록 완료", null));

    }

    //팀 일정 수정
    @PutMapping("/team/events/{eventId}")
    public ResponseEntity<BasicResponseDto> updateTeamEvent(@PathVariable Long eventId,
                                                            @RequestBody TeamEventUpdateRequestDto requestDto,
                                                            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);

        calendarService.updateTeamEvent(eventId, requestDto, userId);
        return ResponseEntity.ok(BasicResponseDto.success("팀 일정 수정 완료", null));
    }

    //팀 일정 삭제
    @DeleteMapping("/team/events/{eventId}")
    public ResponseEntity<BasicResponseDto> deleteTeamEvent(@PathVariable Long eventId,
                                                            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);

        //팀 일정 삭제 처리
        calendarService.deleteTeamEvent(eventId, userId);
        return ResponseEntity.ok(BasicResponseDto.success("팀 일정 삭제 완료", null));
    }

}
