package com.example.chat_service.controller;

import com.example.chat_service.dto.response.TeamInfoDto;
import com.example.chat_service.service.MemberStatusService;
import com.example.chat_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberStatusController {

    private final MemberStatusService memberStatusService;
    private final JwtUtil jwtUtil;

    @GetMapping("/members/status")
    public ResponseEntity<List<TeamInfoDto>> getTeamMembersStatus(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        List<TeamInfoDto> teamInfos =  memberStatusService.getUserStatusByUserId(userId);

        return ResponseEntity.ok(teamInfos);
    }
}
