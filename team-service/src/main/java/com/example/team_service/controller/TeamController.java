package com.example.team_service.controller;

import com.example.team_service.dto.BasicResponseDto;
import com.example.team_service.dto.request.JoinTeamRequestDto;
import com.example.team_service.dto.request.TeamCreateRequestDto;
import com.example.team_service.dto.request.MemberRemoveRequestDto;
import com.example.team_service.dto.request.ValidTokenRequestDto;
import com.example.team_service.dto.response.TeamListResponseDto;
import com.example.team_service.dto.response.TeamManagementResponseDto;
import com.example.team_service.service.TeamService;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("request create team {}", request);
        //토큰에서 userId 가져오기
        Long creatorUserId = jwtUtil.extractedUserIdFromHeader(token);
        log.info("creatorUserId값 확인 {}",creatorUserId);
        teamService.createTeam(request, creatorUserId); // 사용자 ID를 서비스에 전달
        return ResponseEntity.ok(BasicResponseDto.success("팀 생성 완료", null));
    }

    @PostMapping("/invite/{teamId}")
    public ResponseEntity<BasicResponseDto> addTeamMembers(
            @PathVariable("teamId") Long teamId,
            @RequestBody Map<String, List<String>> request,
            @RequestHeader("Authorization") String token) {
        Long requestUserId = jwtUtil.extractedUserIdFromHeader(token);
        List<String> emails = request.get("emails");

        teamService.addTeamMembers(teamId, requestUserId, emails);

        return ResponseEntity.ok(BasicResponseDto.success("팀원 추가 초대가 완료되었습니다.", null));
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

    //팀 마이페이지 - 정보 반환
    @GetMapping("/management/{teamId}")
    public ResponseEntity<BasicResponseDto> getTeamInfo(@PathVariable("teamId") Long teamId) {
        TeamManagementResponseDto responseDto = teamService.getTeamManagementInfo(teamId);

        return ResponseEntity.ok(BasicResponseDto.success("팀 마이페이지 반환 성공", responseDto));
    }

    //팀 마이피에지 - 팀 이름 변경
    @PutMapping("/management/teamName/{teamId}")
    public ResponseEntity<BasicResponseDto> updateTeamName(
            @PathVariable("teamId") Long teamId,
            @RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        String newTeamName = request.get("teamName");

        teamService.updateTeamName(teamId, newTeamName, userId);

        return ResponseEntity.ok(BasicResponseDto.success("팀 이름 변경 성공", null));
    }

    //팀 마이페이지 - 팀 권한 부여
    @PutMapping("/management/grantAdmin/{teamId}")
    public ResponseEntity<BasicResponseDto> grantAdmin(
            @PathVariable("teamId") Long teamId,
            @RequestBody Map<String, Long> request,
            @RequestHeader("Authorization") String token) {
        Long targetUserId = request.get("targetUserId");
        Long requestUserId = jwtUtil.extractedUserIdFromHeader(token);

        teamService.grantAdminRole(teamId, targetUserId, requestUserId);

        return ResponseEntity.ok(BasicResponseDto.success("ADMIN권한이 부여되었습니다.", null));

    }

    //팀 마이페이지 - 팀 권한 삭제
    @PutMapping("/management/grantMember/{teamId}")
    public ResponseEntity<BasicResponseDto> grantMember(
            @PathVariable("teamId") Long teamId,
            @RequestBody Map<String, Long> request,
            @RequestHeader("Authorization") String token) {
        Long targetUserId = request.get("targetUserId");
        Long requestUserId = jwtUtil.extractedUserIdFromHeader(token);

        teamService.grantMemberRole(teamId, targetUserId, requestUserId);

        return ResponseEntity.ok(BasicResponseDto.success("MEMBER권한이 부여되었습니다.", null));
    }

    //팀원 추방
    @DeleteMapping("/memberExpel")
    public ResponseEntity<BasicResponseDto> expelTeamMember(
            @RequestBody MemberRemoveRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        Long requestUserId = jwtUtil.extractedUserIdFromHeader(token);

        teamService.removeTeamMember(requestDto.getTeamId(), requestDto.getUserId(), requestUserId);

        return ResponseEntity.ok(BasicResponseDto.success("팀원 추방이 완료되었습니다.", null));
    }
}