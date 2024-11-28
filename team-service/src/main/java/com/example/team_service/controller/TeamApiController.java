package com.example.team_service.controller;

import com.example.team_service.dto.external.TeamMemberInfoDto;
import com.example.team_service.dto.external.TeamMemberResponseDto;
import com.example.team_service.service.TeamMemberService;
import com.example.team_service.service.TeamService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
@Slf4j
public class TeamApiController {
    //페인 클라이언트 처리 컨트롤러

    private final TeamMemberService teamMemberService;

    @GetMapping("/members/{teamId}")
    public ResponseEntity<TeamMemberResponseDto> getTeamMembers(@PathVariable("teamId") Long teamId) {
        log.info("팀 멤버 정보 요청: teamId = {}", teamId);

        //팀 멤버 조회
        List<TeamMemberInfoDto> members = teamMemberService.getTeamMembers(teamId);
        return ResponseEntity.ok(new TeamMemberResponseDto(teamId, members));
    }
}
