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

    public List<TeamMemberInfoDto> getTeamMembers(Long teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByTeam_TeamId(teamId);
        return teamMembers.stream()
                .map(member -> new TeamMemberInfoDto(
                        member.getUserId(),
                        member.getRole().name()))
                .collect(Collectors.toList());
    }
}
