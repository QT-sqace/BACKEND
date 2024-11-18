package com.example.team_service.service;

import com.example.team_service.client.UserServiceClient;
import com.example.team_service.dto.BasicInfoDto;
import com.example.team_service.dto.TeamCreateRequestDto;
import com.example.team_service.entity.Team;
import com.example.team_service.entity.TeamInvite;
import com.example.team_service.entity.TeamMember;
import com.example.team_service.repository.TeamInviteRepository;
import com.example.team_service.repository.TeamMemberRepository;
import com.example.team_service.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;


    // 팀 생성
    public void createTeam(TeamCreateRequestDto request, Long creatorUserId) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        log.info("team레포 시작");
        // 팀 생성 시 생성자가 관리자
        Team team = new Team(request.getProjectName(), encodedPassword, request.getProjectImage(), creatorUserId);
        teamRepository.save(team);

        log.info("팀 멤버 레포 시작");
        // 생성자를 팀 멤버로 추가하면서 관리자 역할로 설정
        TeamMember teamMember = new TeamMember(team, creatorUserId, "admin", LocalDateTime.now());
        teamMemberRepository.save(teamMember);
    }

    // 초대 발송: 초대할 때마다 토큰 생성, 만료 시간 설정 및 이메일 전송
    // 이메일 전송 기능은 아직 구현 X
    public String sendInvitation(String email, Long teamId) {
        // 초대 토큰과 만료 시간 생성
        String inviteToken = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(24); // 24시간 후 만료

        // 초대 토큰 정보 반환 (이메일로 전송될 링크)
        return "http://localhost:8000/invite/" + inviteToken;
    }

    // 초대 토큰을 사용하여 비밀번호 검증 후 멤버 추가
    public void validateInviteTokenAndAddUser(String inviteToken, String password, Long user_id) {
        // 초대 토큰을 이용해 팀 조회
        TeamInvite invite = teamInviteRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token."));

        // 초대 토큰 만료 시간 검증
        if (invite.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expired invite token.");
        }

        Team team = invite.getTeam(); // 초대에 연결된 팀을 가져옴

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, team.getPassword())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        // UserServiceClient를 통해 유저 기본 정보 조회
        BasicInfoDto userBasicInfo = userServiceClient.getUserBasicInfo(user_id);
        if (userBasicInfo == null) {
            throw new IllegalArgumentException("User not found.");
        }

        // 팀 멤버로 추가
        TeamMember teamMember = new TeamMember(team, user_id, "member", LocalDateTime.now());
        teamMemberRepository.save(teamMember);
    }

}
