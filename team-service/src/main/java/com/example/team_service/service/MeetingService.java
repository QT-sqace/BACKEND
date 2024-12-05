package com.example.team_service.service;

import com.example.team_service.dto.response.MeetingDTO;
import com.example.team_service.entity.Meeting;
import com.example.team_service.entity.Team;
import com.example.team_service.entity.TeamMember;
import com.example.team_service.repository.MeetingRepository;
import com.example.team_service.repository.TeamMemberRepository;
import com.example.team_service.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    // 미팅룸 생성
    public MeetingDTO createMeeting(Long teamId, Long userId, String meetingName, String meetingUrl, String userName) {
        // Team 및 TeamMember 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀을 찾을 수 없습니다: " + teamId));
        TeamMember creator = teamMemberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + userId));

        // Meeting 엔티티 생성 및 저장
        Meeting meeting = new Meeting(team, creator, meetingName, meetingUrl);
        meeting.setUserName(userName); // user_name 저장
        Meeting savedMeeting = meetingRepository.save(meeting);

        return convertToMeetingDTO(savedMeeting);
    }


    // 미팅룸 조회 (meeting_id로 조회)
    public MeetingDTO getMeetingById(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("미팅룸을 찾을 수 없습니다: " + meetingId));

        return convertToMeetingDTO(meeting);
    }

    // 미팅룸 목록 조회 (team_id로 조회 -> 전체조회)
    public List<MeetingDTO> getMeetingsByTeamId(Long teamId) {
        List<Meeting> meetings = meetingRepository.findByTeam_TeamId(teamId);

        return meetings.stream()
                .map(this::convertToMeetingDTO)
                .collect(Collectors.toList());
    }

    // 미팅룸 삭제
    public void deleteMeeting(Long meetingId) {
        // Meeting 엔티티 확인 및 삭제
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("미팅룸을 찾을 수 없습니다: " + meetingId));

        meetingRepository.delete(meeting);
    }

    // Meeting 엔티티 -> MeetingDTO 변환
    private MeetingDTO convertToMeetingDTO(Meeting meeting) {
        return MeetingDTO.builder()
                .meetingId(meeting.getMeetingId())
                .teamId(meeting.getTeam().getTeamId())
                .createdBy(meeting.getCreatedBy().getTeamMemberId())
                .meetingName(meeting.getMeetingName())
                .meetingUrl(meeting.getMeetingUrl())
                .userName(meeting.getUserName()) // userName
                .meetingDate(meeting.getMeetingDate())
                .build();
    }

}
