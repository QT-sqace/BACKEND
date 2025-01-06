package com.example.calendar_service.service;

import com.example.calendar_service.client.TeamServiceClient;
import com.example.calendar_service.dto.external.TeamMemberInfoDto;
import com.example.calendar_service.dto.external.TeamMemberResponseDto;
import com.example.calendar_service.dto.team.TeamEventRequestDto;
import com.example.calendar_service.dto.team.TeamEventResponseDto;
import com.example.calendar_service.dto.team.TeamEventUpdateRequestDto;
import com.example.calendar_service.entity.Calendar;
import com.example.calendar_service.entity.CalendarInfo;
import com.example.calendar_service.entity.CalendarShared;
import com.example.calendar_service.repository.CalendarInfoRepository;
import com.example.calendar_service.repository.CalendarRepository;
import com.example.calendar_service.repository.CalendarSharedRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarInfoRepository calendarInfoRepository;
    private final TeamServiceClient teamServiceClient;
    private final CalendarSharedRepository calendarSharedRepository;

    //팀 생성시 캘린더 생성
    public void createCalendarForTeam(Long teamId) {
        Calendar calendar = new Calendar(teamId, true);
        calendarRepository.save(calendar);
    }

    //팀 일정 조회
    public List<TeamEventResponseDto> getTeamEvents(Long teamId) {
        Calendar teamCalendar = calendarRepository.findByTeamId(teamId);
        if(teamCalendar == null) {
            throw new IllegalArgumentException("해당 팀 캘린더가 존재하지 않습니다.");
        }

        return calendarInfoRepository.findByCalendarAndEventType(teamCalendar, CalendarInfo.EventType.TEAM)
                .stream()
                .map(event -> new TeamEventResponseDto(
                        event.getEventId(),
                        event.getTitle(),
                        event.getContent(),
                        event.getAllDay(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getColor(),
                        event.getEventType()
                ))
                .collect(Collectors.toList());
    }

    //팀 일정 등록
    public void createTeamEvent(TeamEventRequestDto requestDto, Long userId) {

        //FeignClient로 팀 멤버 정보 가져오기
        TeamMemberResponseDto teamMemberResponse = teamServiceClient.getTeamMembers(requestDto.getTeamId());

        //현재 요청자의 role 확인
        String role = teamMemberResponse.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .map(TeamMemberInfoDto::getRole)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버가 아닙니다."));

        //권한 검증
        if (!role.equals("ADMIN") && !role.equals("MASTER")) {
            throw new IllegalArgumentException("일정 등록 권한이 없습니다.");
        }

        //팀 캘린더 가져오기
        Calendar teamCalendar = calendarRepository.findByTeamId(requestDto.getTeamId());
        if (teamCalendar == null) {
            throw new IllegalArgumentException("해당 팀 캘린더가 존재하지 않습니다.");
        }

        LocalDateTime startDateKST = ZonedDateTime.parse(requestDto.getStartDate())
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        LocalDateTime endDateKST = ZonedDateTime.parse(requestDto.getEndDate())
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        CalendarInfo teamEvent = new CalendarInfo(
                teamCalendar,
                requestDto.getTitle(),
                requestDto.getContent(),
                startDateKST.toString(),
                endDateKST.toString(),
//                requestDto.getStartDate(),
//                requestDto.getEndDate(),
                requestDto.getAllDay(),
                requestDto.getColor(),
                CalendarInfo.EventType.TEAM
        );

        calendarInfoRepository.save(teamEvent);

        //팀 멤버의 개인 캘린더에 연결
        for (TeamMemberInfoDto member : teamMemberResponse.getMembers()) {
            Calendar personalCalendar = calendarRepository.findByUserId(member.getUserId());
            if (personalCalendar != null) {
                CalendarShared sharedEvent = new CalendarShared(
                        personalCalendar.getCalendarId(),   //개인 캘린더 ID
                        teamEvent.getEventId()              //팀 이벤트 ID
                );
                calendarSharedRepository.save(sharedEvent);
            }
        }
    }

    //팀 일정 수정
    public void updateTeamEvent(Long eventId, TeamEventUpdateRequestDto requestDto, Long userId) {
        //팀 일정 가져오기
        CalendarInfo teamEvent = calendarInfoRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

        //팀 권한 검증
        TeamMemberResponseDto teamMembers = teamServiceClient.getTeamMembers(requestDto.getTeamId());
        String role = teamMembers.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .map(member -> member.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버가 아닙니다."));

        if (!role.equals("ADMIN") && !role.equals("MASTER")) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        LocalDateTime startDateKST = ZonedDateTime.parse(requestDto.getStartDate())
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        LocalDateTime endDateKST = ZonedDateTime.parse(requestDto.getEndDate())
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        //수정 로직
        teamEvent.updateEvent(
                requestDto.getTitle(),
                requestDto.getContent(),
                startDateKST.toString(),
                endDateKST.toString(),
//                requestDto.getStartDate(),
//                requestDto.getEndDate(),
                requestDto.getAllDay(),
                requestDto.getColor()
        );

        calendarInfoRepository.save(teamEvent);
    }

    //팀 일정 삭제
    public void deleteTeamEvent(Long eventId, Long userId) {
        //팀 일정 가져오기
        CalendarInfo teamEvent = calendarInfoRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));

        //팀 권한 검증
        TeamMemberResponseDto teamMembers = teamServiceClient.getTeamMembers(teamEvent.getCalendar().getTeamId());
        String role = teamMembers.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .map(member -> member.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("팀 멤버가 아닙니다."));

        if (!role.equals("ADMIN") && !role.equals("MASTER")) {
            throw new IllegalStateException("일정 삭제 권한이 없습니다.");
        }
        //팀 공유 db에도 해당 일정 삭제
        calendarSharedRepository.deleteByEventId(eventId);

        //일정 삭제
        calendarInfoRepository.delete(teamEvent);
    }

    //userId값으로 팀원인지 확인하는 메서드
    public void verifyTeamMember(Long teamId, Long userId) {
        TeamMemberResponseDto teamMembers = teamServiceClient.getTeamMembers(teamId);
        boolean isMember = teamMembers.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(userId));

        if (!isMember) {
            throw new IllegalArgumentException("해당 팀의 멤버가 아니므로 접근할 수 없습니다.");
        }
    }

    //팀 일정과 개인 캘린더 동기화 - 최초 팀 가입시점
    public void syncTeamEventsToPersonalCalendar(Long teamId, Long userId) {
        //팀 캘린더 조회
        Calendar teamCalendar = calendarRepository.findByTeamId(teamId);
        if (teamCalendar == null) {
            throw new IllegalArgumentException("해당 팀의 캘린더가 존재하지 않습니다.");
        }

        //TEAM 일정만 가져옴
        List<CalendarInfo> teamEvents = calendarInfoRepository.findByCalendarAndEventType(
                teamCalendar, CalendarInfo.EventType.TEAM
        );

        //개인 캘린더 조회
        Calendar personalCalendar = calendarRepository.findByUserId(userId);
        if (personalCalendar == null) {
            throw new IllegalArgumentException("해당 사용자의 개인 캘린더가 존재하지 않습니다.");
        }

        //각 팀 일정과 개인 캘린더 동기화
        for (CalendarInfo event : teamEvents) {
            //팀 이벤트가 이미 동기화 되어있는지 확인
            if (!calendarSharedRepository.existsByCalendarIdAndEventId(
                    personalCalendar.getCalendarId(), event.getEventId()
            )) {
                //동기화 없다면 calendarShared 엔티티 생성 후 저장
                CalendarShared sharedEvent = new CalendarShared(
                        personalCalendar.getCalendarId(),
                        event.getEventId()
                );
                calendarSharedRepository.save(sharedEvent);
            }
        }
    }
}
