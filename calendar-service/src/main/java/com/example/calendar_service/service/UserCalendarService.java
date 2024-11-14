package com.example.calendar_service.service;

import com.example.calendar_service.dto.user.UserEventRequestDto;
import com.example.calendar_service.dto.user.UserEventResponseDto;
import com.example.calendar_service.entity.Calendar;
import com.example.calendar_service.entity.CalendarInfo;
import com.example.calendar_service.repository.CalendarInfoRepository;
import com.example.calendar_service.repository.CalendarRepository;
import com.example.calendar_service.repository.CalendarSharedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarInfoRepository calendarInfoRepository;
    private final CalendarSharedRepository calendarSharedRepository;

    //처음 회원가입시 캘린더 생성
    public void createCalendarForUser(Long userId) {
        Calendar calendar = new Calendar(userId);
        calendarRepository.save(calendar);
    }

    //개인 일정 등록
    public void createEvent(Long userId, UserEventRequestDto dto) {
        Calendar calendar = calendarRepository.findByUserId(userId);
        CalendarInfo event = new CalendarInfo(calendar, dto.getTitle(), dto.getContent(),
                dto.getStartDate(), dto.getEndDate(), dto.getAllDay(),dto.getColor(),
                CalendarInfo.EventType.PERSONAL);

        calendarInfoRepository.save(event);
    }

    //캘린더 일정 가져오기
    public List<UserEventResponseDto> getUserEvents(Long userId) {
        Calendar calendar = calendarRepository.findByUserId(userId);

        // 개인 캘린더의 개인 일정 조회
        List<UserEventResponseDto> personalEvents =
                calendarInfoRepository.findByCalendarAndEventType(calendar, CalendarInfo.EventType.PERSONAL)
                .stream()  // CalendarInfo 목록을 스트림으로 변환
                .map(event -> new UserEventResponseDto(  // 각 CalendarInfo 객체를 UserEventResponseDto로 변환합니다.
                        event.getEventId(),
                        event.getTitle(),
                        event.getContent(),
                        event.getAllDay(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getColor(),
                        event.getEventType()))
                .collect(Collectors.toList());  // 변환된 UserEventResponseDto 객체들을 리스트로 수집합니다.


        //개인 캘린더에 팀 일정 조회
        List<UserEventResponseDto> teamEvents = calendarSharedRepository.findByCalendarId(calendar.getCalendarId())
                .stream() //CalendarShared 목록을 스트림으로 변환
                .map(shared -> calendarInfoRepository.findById(shared.getEventId()).orElse(null))   //각 공유된 팀일정의 CalendarInfo 찾기
                .filter(event -> event != null && event.getEventType() == CalendarInfo.EventType.TEAM)
                .map(event -> new UserEventResponseDto( //필터링된 CalendarInfo 객체를 UserEventResponseDto로 변환
                        event.getEventId(),
                        event.getTitle(),
                        event.getContent(),
                        event.getAllDay(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getColor(),
                        event.getEventType()))
                .collect(Collectors.toList());  //변환된 UserEventResponseDto 객체들을 리스트로 수집

        //개인 일정과 팀 일정 합치기
        List<UserEventResponseDto> allEvents = new ArrayList<>();
        allEvents.addAll(personalEvents);
        allEvents.addAll(teamEvents);

        return allEvents;
    }
}
