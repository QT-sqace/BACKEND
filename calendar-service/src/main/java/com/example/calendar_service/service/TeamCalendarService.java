package com.example.calendar_service.service;

import com.example.calendar_service.dto.team.TeamEventRequestDto;
import com.example.calendar_service.entity.Calendar;
import com.example.calendar_service.entity.CalendarInfo;
import com.example.calendar_service.repository.CalendarInfoRepository;
import com.example.calendar_service.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamCalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarInfoRepository calendarInfoRepository;

    //팀 생성시 캘린더 생성
    public void createCalendarForTeam(Long teamId) {
        Calendar calendar = new Calendar(teamId, true);
        calendarRepository.save(calendar);
    }

/*    //팀 일정 등록
    public void createTeamEvent(TeamEventRequestDto requestDto, Long userId) {
        Calendar calendar = calendarRepository.findByTeamId(requestDto.getTeamId());
        if (calendar == null) {
            throw new IllegalArgumentException("해당 팀 캘린더가 존재하지 않습니다.");
        }

        CalendarInfo teamEvent = new CalendarInfo(
                calendar,
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getAllDay(),
                requestDto.getColor(),
                CalendarInfo.EventType.TEAM
        );

        calendarInfoRepository.save(teamEvent);

        //팀 멤버의 개인 캘린더에 연결
        List<Long> memberIds = getTeamMemberIds()
    }*/
}
