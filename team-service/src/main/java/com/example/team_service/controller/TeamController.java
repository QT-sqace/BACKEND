package com.example.team_service.controller;

import com.example.team_service.dto.BasicResponseDto;
import com.example.team_service.dto.request.JoinTeamRequestDto;
import com.example.team_service.dto.request.TeamCreateRequestDto;
import com.example.team_service.dto.request.ValidTokenRequestDto;
import com.example.team_service.dto.response.TeamListResponseDto;
import com.example.team_service.service.TeamService;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
@Slf4j
public class TeamController {

    private final TeamService teamService;
    private final JwtUtil jwtUtil;

    //팀 생성 요청
    @PostMapping("/create")
    public ResponseEntity<BasicResponseDto> createTeam(@RequestBody TeamCreateRequestDto request,
                                             @RequestHeader("Authorization") String token) {
        //토큰에서 userId 가져오기
        Long creatorUserId = jwtUtil.extractedUserIdFromHeader(token);
        teamService.createTeam(request, creatorUserId); // 사용자 ID를 서비스에 전달
        return ResponseEntity.ok(BasicResponseDto.success("팀 생성 완료", null));
    }

    //초대 토큰 확인
    @PostMapping("/validate")
    public ResponseEntity<BasicResponseDto> validateInviteToken(@RequestBody ValidTokenRequestDto request) {
        Map<String, Object> response = teamService.validateInviteToken(request.getInviteToken());
        return ResponseEntity.ok(BasicResponseDto.success("초대 토큰이 유효합니다.", response));
    }

    //팀 가입 요청 - 패스워드 검사
    @PostMapping("/join")
    public ResponseEntity<BasicResponseDto> joinTeam(@RequestBody JoinTeamRequestDto request,
                                                     @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        log.info("userId값 확인 {}",userId);
        teamService.joinTeam(request.getInviteToken(), request.getPassword(), userId);
        return ResponseEntity.ok(BasicResponseDto.success("팀에 성공적으로 가입되었습니다.", null));
    }

    //본인 팀리스트 조회
    @GetMapping("/my-teams")
    public ResponseEntity<BasicResponseDto<List<TeamListResponseDto>>> getTeams(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        List<TeamListResponseDto> teams = teamService.getTeamsByUserId(userId);

        return ResponseEntity.ok(BasicResponseDto.success("팀 목록 조회 성공", teams));
    }

}