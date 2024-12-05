package com.example.team_service.controller;

import com.example.team_service.entity.TeamMember;
import com.example.team_service.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team-members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    // 특정 팀 멤버 조회 (ID 기반)
    @GetMapping("/{teamMemberId}")
    public ResponseEntity<TeamMember> getTeamMemberById(@PathVariable Long teamMemberId) {
        TeamMember teamMember = teamMemberService.getTeamMemberById(teamMemberId);
        return ResponseEntity.ok(teamMember);
    }

    // 특정 팀의 모든 멤버 조회
    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<List<TeamMember>> getAllTeamMembersByTeamId(@PathVariable Long teamId) {
        List<TeamMember> teamMembers = teamMemberService.getAllTeamMembersByTeamId(teamId);
        return ResponseEntity.ok(teamMembers);
    }


    // 특정 유저의 멤버십 정보 조회
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<TeamMember>> getAllTeamsByUserId(@PathVariable Long userId) {
        List<TeamMember> memberships = teamMemberService.getAllTeamsByUserId(userId);
        return ResponseEntity.ok(memberships);
    }
}
