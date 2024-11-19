package com.example.team_service.service;

import com.example.team_service.dto.request.TeamCreateRequestDto;
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
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    // 팀 생성, 추후에 팀 로고 이미지를 MinIo 경로로 등록하는 로직 필요
    public void createTeam(TeamCreateRequestDto request, Long creatorUserId) {

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Team team = new Team(
                request.getProjectName(),
                encodedPassword,
                request.getProjectImage()
        );
        teamRepository.save(team);

        // 팀 생성자를 teamMember DB에 추가
        TeamMember masterMember = new TeamMember(
                team,
                creatorUserId,
                TeamMember.Role.MASTER,
                LocalDateTime.now()
        );
        teamMemberRepository.save(masterMember);

        //초대 링크 발송
        for (String email : request.getEmails()) {
            String inviteToken = UUID.randomUUID().toString();
            TeamInvite invite = new TeamInvite(inviteToken, team);
            teamInviteRepository.save(invite);

            //추후에 배포할때는 https://yourdomain.com/invite/{inviteToken} 여기로 수정
            String inviteLink = "http://localhost:3000/invite/" + inviteToken;
            emailService.sendEmail(email, inviteLink);
            log.info("보낸 이메일: {}, 초대 링크: {}", email, inviteLink);
        }
        log.info("팀 초대 완료: {}" , request.getProjectName());
    }

    //InviteToken 검증
    public Map<String, Object> validateInviteToken(String inviteToken) {
        //초대 토큰 조회
        TeamInvite invite = teamInviteRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> {
                    log.error("유효하지 않은 토큰값 : {}", inviteToken);
                    return new IllegalArgumentException("유효하지 않은 초대 토큰입니다.");
                });

        // 만료 시간 검증
        if (invite.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.error("만료된 토큰값 : {}", inviteToken);
            throw new IllegalArgumentException("초대 토큰이 만료되었습니다.");
        }

        // 유효한 초대 토큰에 대한 팀 정보 반환
        log.info("성공적인 invite토큰값 : {}", inviteToken);
        return Map.of(
                "teamId", invite.getTeam().getTeamId(),
                "projectName", invite.getTeam().getProjectName()
        );
    }

    //팀 가입 - 패스워드 확인 -> inviteToken으로 팀조회 후 팀 가입
    public void joinTeam(String inviteToken, String password, Long userId) {
        log.info("joinTeam 메서드 시작");
        //초대 토큰 조회
        TeamInvite invite = teamInviteRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 토큰"));

        //초대 토큰 만료시간 확인
        if (invite.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.error("만료된 토큰입니다: {}", inviteToken);
            throw new IllegalArgumentException("초대 토큰이 만료되었습니다.");
        }

        //팀 비밀번호 확인
        Team team = invite.getTeam();
        if (!passwordEncoder.matches(password, team.getTeamPassword())) {
            log.error("유효하지 않은 팀 패스워드 팀은: {}", team.getProjectName());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        //팀 멤버로 추가
        TeamMember newMember = new TeamMember(team, userId, TeamMember.Role.MEMBER, LocalDateTime.now());
        teamMemberRepository.save(newMember);

        log.info("회원번호: {} 성공적으로 팀에 가입 팀명: {}  ", userId, team.getProjectName());

    }
}
