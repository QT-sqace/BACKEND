package com.example.team_service.service;

import com.example.team_service.client.CalendarServiceClient;
import com.example.team_service.client.ChatServiceClient;
import com.example.team_service.client.UserServiceClient;
import com.example.team_service.dto.external.BasicInfoDto;
import com.example.team_service.dto.external.ChatParticipantAddRequestDto;
import com.example.team_service.dto.external.TeamCalendarRequestDto;
import com.example.team_service.dto.external.TeamChatRequestDto;
import com.example.team_service.dto.request.TeamCreateRequestDto;
import com.example.team_service.dto.response.TeamListResponseDto;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CalendarServiceClient calendarServiceClient;
    private final UserServiceClient userServiceClient;
    private final ChatServiceClient chatServiceClient;
    private final ExecutorService executorService;  //비동기 처리용


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

        //팀 캘린더 생성 요청 (Feign Client 호출)
        TeamCalendarRequestDto requestDto = new TeamCalendarRequestDto(team.getTeamId());
        calendarServiceClient.createTeamCalendar(requestDto);

        //팀 생성자 프로필 요청 (Feign Client 호출)
        String creatorProfileImage = userServiceClient.getUserProfile(masterMember.getUserId());
        BasicInfoDto basicInfo = userServiceClient.getUserBasicInfo(masterMember.getUserId());
        String userName = basicInfo.getUserName();
        log.info("userName 가져오는거 확인: {}", userName);

        //팀 채팅방 생성 요청 (Feign Client 호출)
        TeamChatRequestDto teamChatRequestDto = new TeamChatRequestDto(team.getTeamId(), team.getProjectName(), team.getProjectImage(),
                masterMember.getUserId(),userName,creatorProfileImage);
        chatServiceClient.createRoom(teamChatRequestDto);


        //초대 링크 발송
        for (String email : request.getEmails()) {
            String inviteToken = UUID.randomUUID().toString();
            TeamInvite invite = new TeamInvite(inviteToken, team);
            teamInviteRepository.save(invite);

            //추후에 배포할때는 https://yourdomain.com/invite/{inviteToken} 여기로 수정
            String inviteLink = "http://sqace.site/invite/" + inviteToken;
            emailService.sendEmail(email, inviteLink);
            log.info("보낸 이메일: {}, 초대 링크: {}", email, inviteLink);
        }
        log.info("팀 초대 완료: {}", request.getProjectName());
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

        //가입한 멤버의 프로필 경로 가져오기 feign 호출
        String profileImage = userServiceClient.getUserProfile(newMember.getUserId());
        BasicInfoDto basicInfo = userServiceClient.getUserBasicInfo(newMember.getUserId());
        String userName = basicInfo.getUserName();
        log.info("userName 가져오는거 확인: {}", userName);

        //팀 채팅방에 추가시키기
        ChatParticipantAddRequestDto requestDto = new ChatParticipantAddRequestDto(
                team.getTeamId(), userId,userName, profileImage);
        chatServiceClient.addParticipant(requestDto);

        log.info("회원번호: {} 성공적으로 팀에 가입 팀명: {}  ", userId, team.getProjectName());

        executorService.submit(() -> syncTeamEventAsync(team.getTeamId(), userId));
    }

    //팀리스트 반환
    public List<TeamListResponseDto> getTeamsByUserId(Long userId) {
        log.info("getTeamsByUserId 호출 - userId: {}", userId);


        // 사용자가 가입된 팀 목록 조회
        List<TeamMember> teamMembers = teamMemberRepository.findAllByUserId(userId);
        if (teamMembers.isEmpty()) {
            log.warn("사용자가 가입된 팀이 없습니다. userId: {}", userId);
            return List.of(); // 빈 리스트 반환
        }

        // 팀 정보 DTO 변환
        List<TeamListResponseDto> teamList = teamMembers.stream()
                .map(member -> {
                    Team team = member.getTeam();

                    // 각 팀 멤버들의 프로필 이미지 경로 조회
                    List<String> memberImages = team.getMembers().stream()
                            .map(m -> userServiceClient.getUserProfile(m.getUserId()))
                            .toList();

                    return new TeamListResponseDto(
                            team.getTeamId(),
                            team.getProjectName(),
                            team.getProjectImage(),
                            member.getRole().name(),
                            team.getMembers().size(),
                            memberImages
                    );
                })
                .toList();

        log.info("사용자 ID {}에 대한 팀 목록 반환 완료. 팀 개수: {}", userId, teamList.size());
        return teamList;
    }

    /**
     * 팀에 가입시점에 최초 한번만 호출
     * 비동기 동기화로 추후에 팀에 가입한 회원도 개인 캘린더에 팀 일정 가져오기
     */
    private void syncTeamEventAsync(Long teamId, Long userId) {
        try {
            log.info("팀 일정 동기화 시작 - teamId: {}, userId: {}", teamId, userId);
            calendarServiceClient.syncTeamEventsToPersonalCalendar(teamId, userId);
            log.info("팀 일정 동기화 완료 - teamId: {}, userId: {}", teamId, userId);
        } catch (Exception e) {
            log.error("팀 일정 동기화 실패 - teamId: {}, userId: {}", teamId, userId, e);
        }
    }
}
