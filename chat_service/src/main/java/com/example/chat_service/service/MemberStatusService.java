package com.example.chat_service.service;

import com.example.chat_service.common.UserStatusManager;
import com.example.chat_service.dto.response.TeamInfoDto;
import com.example.chat_service.dto.response.TeamMemberDto;
import com.example.chat_service.entity.ChatParticipant;
import com.example.chat_service.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberStatusService {

    private final ChatParticipantRepository participantRepository;
    private final UserStatusManager statusManager;

    public List<TeamInfoDto> getUserStatusByUserId(Long userId) {
        // 사용자가 속한 모든 팀의 참여 정보 가져오기
        List<ChatParticipant> userParticipation = participantRepository.findByUserId(userId);

        if (userParticipation.isEmpty()) {
            throw new IllegalArgumentException("사용자가 속한 팀이 없습니다.");
        }

        // 사용자가 속한 팀 ID 목록 추출
        List<Long> teamIds = userParticipation.stream()
                .map(participant -> participant.getRoom().getTeamId())
                .distinct()
                .collect(Collectors.toList());

        // 각 팀의 정보를 TeamInfoDto로 변환하여 반환
        return teamIds.stream()
                .map(teamId -> getTeamInfo(teamId, userId)) // 팀 ID와 본인 userId를 전달
                .collect(Collectors.toList());
    }

    private TeamInfoDto getTeamInfo(Long teamId, Long userId) {
        // 해당 팀의 모든 참여자 정보 조회
        List<ChatParticipant> participants = participantRepository.findByRoom_TeamId(teamId);

        // 본인을 제외한 팀 멤버 정보 생성
        List<TeamMemberDto> members = participants.stream()
                .filter(participant -> !participant.getUserId().equals(userId)) // 본인 제외
                .map(participant -> new TeamMemberDto(
                        participant.getUserId(),
                        participant.getUserName(),
                        participant.getProfileImage(),
                        statusManager.getStatus(participant.getUserId())
                ))
                .collect(Collectors.toList());

        // 팀 이름 가져오기
        String teamName = participants.isEmpty() ? "Unknown Team" : participants.get(0).getRoom().getRoomName();

        return new TeamInfoDto(teamId, teamName, members);
    }
}
