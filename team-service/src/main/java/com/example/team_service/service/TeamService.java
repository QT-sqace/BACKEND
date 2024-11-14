package com.example.team_service.service;

import com.example.team_service.client.UserServiceClient;
import com.example.team_service.dto.external.BasicInfoDto;
import com.example.team_service.dto.request.auth.TeamCreateRequestDto;
import com.example.team_service.entity.Team;
import com.example.team_service.entity.TeamInvite;
import com.example.team_service.entity.TeamMember;
import com.example.team_service.repository.TeamInviteRepository;
import com.example.team_service.repository.TeamMemberRepository;
import com.example.team_service.repository.TeamRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       TeamInviteRepository teamInviteRepository,
                       UserServiceClient userServiceClient,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamInviteRepository = teamInviteRepository;
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 팀 생성
    public void createTeam(TeamCreateRequestDto request, Long creatorUserId) {
        // DTO 필드 이름과 메서드 이름을 일치하도록 수정
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 팀 생성 및 생성자를 관리자 역할로 설정
        Team team = new Team(request.getProjectName(), encodedPassword, request.getProjectImage(), creatorUserId);
        teamRepository.save(team);

        TeamMember teamMember = new TeamMember(team, creatorUserId, "admin", LocalDateTime.now());
        teamMemberRepository.save(teamMember);
    }

    // 초대 발송: 토큰 생성, 만료 시간 설정 및 이메일 전송
    public String sendInvitation(String email, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        // 초대 토큰 및 만료 시간 생성
        String inviteToken = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(24); // 24시간 후 만료

        TeamInvite invite = new TeamInvite(inviteToken, expirationTime, team);
        teamInviteRepository.save(invite);

        String inviteLink = "http://localhost:8000/invite/" + inviteToken;
        emailService.sendInviteEmail(email, inviteLink); // 이메일 발송

        return inviteLink;
    }

    // 초대 토큰 검증 및 팀 멤버 추가
    public void validateInviteTokenAndAddUser(String inviteToken, String password, Long userId) {
        TeamInvite invite = teamInviteRepository.findByInviteTokenAndExpirationTimeAfter(inviteToken, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 초대 토큰입니다."));


        Team team = invite.getTeam();

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, team.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 유저 정보 확인 및 멤버 추가
        BasicInfoDto userBasicInfo = userServiceClient.getUserBasicInfo(userId);
        if (userBasicInfo == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 이미 멤버로 존재하는지 확인
        if (teamMemberRepository.existsByTeamAndUserId(team, userId)) {
            throw new IllegalArgumentException("이미 팀의 멤버입니다.");
        }

        TeamMember teamMember = new TeamMember(team, userId, "member", LocalDateTime.now());
        teamMemberRepository.save(teamMember);
    }
}
