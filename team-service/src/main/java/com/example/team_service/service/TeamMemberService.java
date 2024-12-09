package com.example.team_service.service;

import com.example.team_service.dto.external.TeamMemberInfoDto;
import com.example.team_service.entity.TeamMember;
import com.example.team_service.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    // ID로 특정 멤버 조회
    public TeamMember getTeamMemberById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 팀 멤버를 찾을 수 없습니다."));
    }

    // 특정 팀의 모든 멤버 조회
    public List<TeamMember> getAllTeamMembersByTeamId(Long teamId) {
        return teamMemberRepository.findAllByTeamTeamId(teamId);
    }

    // 특정 유저가 속한 팀 멤버십 정보 조회
    public List<TeamMember> getAllTeamsByUserId(Long userId) {
        return teamMemberRepository.findAllByUserId(userId);
    }

    // 특정 팀의 멤버 정보를 DTO로 반환
    public List<TeamMemberInfoDto> getTeamMembers(Long teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamTeamId(teamId);
        return teamMembers.stream()
                .map(member -> new TeamMemberInfoDto(
                        member.getUserId(),
                        member.getRole().name()))
                .collect(Collectors.toList());
    }
}
