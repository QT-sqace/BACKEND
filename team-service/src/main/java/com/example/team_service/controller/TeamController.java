package com.example.team_service.controller;

import com.example.team_service.dto.TeamCreateRequestDto;
import com.example.team_service.service.TeamService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTeam(@RequestBody TeamCreateRequestDto request) {
        if (!request.getPassword().equals(request.getConfirm_password())) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        // 현재 사용자 ID를 SecurityContextHolder에서 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long creatorUserId = Long.parseLong(authentication.getName()); // 현재 사용자 ID 가져오기

        teamService.createTeam(request, creatorUserId); // 사용자 ID를 서비스에 전달
        return ResponseEntity.ok("Team created successfully.");
    }

    //구체적인 초대 이메일 발송기능은 아직 구현 X, 보내야 할 URL만 생성해줌
    @PostMapping("/invite")
    public ResponseEntity<String> sendInvitation(@RequestParam String email, @RequestParam Long teamId) {
        String inviteLink = teamService.sendInvitation(email, teamId);
        return ResponseEntity.ok("Invitation sent to " + email + ". Link: " + inviteLink);
    }

    @PostMapping("/invite/{InviteToken}")
    public ResponseEntity<String> validateInviteAndAddUser(
            @PathVariable String InviteToken,
            @RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            Long userId = Long.parseLong(request.get("userId"));
            teamService.validateInviteTokenAndAddUser(InviteToken, password, userId);
            return ResponseEntity.ok("User added to workspace.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}