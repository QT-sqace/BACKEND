package com.example.calendar_service.controller;

import com.example.calendar_service.dto.BasicResponseDto;
import com.example.calendar_service.dto.user.UserEventRequestDto;
import com.example.calendar_service.dto.user.UserCalendarRequestDto;
import com.example.calendar_service.dto.user.UserEventResponseDto;
import com.example.calendar_service.service.UserCalendarService;
import com.example.calendar_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserCalendarController {

    private final UserCalendarService calendarService;
    private final JwtUtil jwtUtil;

    //유저 서비스에서 Feign으로 요청
    @PostMapping("/create/personal")
    public ResponseEntity<Void> createPersonalCalendar(@RequestBody UserCalendarRequestDto requestDto) {
        calendarService.createCalendarForUser(requestDto.getUserId());
        log.info("개인 캘린더 생성완료");
        return ResponseEntity.ok().build();
    }

    //개인 일정 조회
    @GetMapping("/events")
    public ResponseEntity<BasicResponseDto> getUserEvents(@RequestHeader("Authorization") String token) {
        //토큰에서 userID값 추출
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractedUserId(jwt); //userId값 추출
        log.info("userId 확인 ={}", userId);

        //개인 일정과 팀 일정 모두 조회
        List<UserEventResponseDto> events = calendarService.getUserEvents(userId);
        return ResponseEntity.ok(BasicResponseDto.success("일정 조회 완료", events));
    }

    //개인 일정 등록
    @PostMapping("/events")
    public ResponseEntity<BasicResponseDto> createEvent(@RequestBody UserEventRequestDto requestDto,
                                                        @RequestHeader("Authorization") String token) {
        //토큰에서 userID값 추출
        String jwt = token.substring(7);
        Long userId = jwtUtil.extractedUserId(jwt); //userId값 추출
        log.info("userId 확인 ={}", userId);
        calendarService.createEvent(userId, requestDto);
        return ResponseEntity.ok(BasicResponseDto.success("이벤트 생성 완료",null));
    }

}
