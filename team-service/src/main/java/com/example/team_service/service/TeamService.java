package com.example.team_service.service;

import com.example.team_service.client.CalendarServiceClient;
import com.example.team_service.client.ChatServiceClient;
import com.example.team_service.client.UserServiceClient;
import com.example.team_service.common.UserStatusManager;
import com.example.team_service.dto.external.*;
import com.example.team_service.dto.request.TeamCreateRequestDto;
import com.example.team_service.dto.response.*;
import com.example.team_service.entity.Notice;
import com.example.team_service.entity.Team;
import com.example.team_service.entity.TeamInvite;
import com.example.team_service.entity.TeamMember;
import com.example.team_service.repository.NoticeRepository;
import com.example.team_service.repository.TeamInviteRepository;
import com.example.team_service.repository.TeamMemberRepository;
import com.example.team_service.repository.TeamRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
    private final MinioClient minioClient;
    private final UserStatusManager userStatusManager;
    private final NoticeRepository noticeRepository;


    @Value("${minio.bucket.user-profile}")
    private String profileBucket;

    @Value("${minio.server.url}")
    private String minioUrl;

    // 팀 생성, 추후에 팀 로고 이미지를 MinIo 경로로 등록하는 로직 필요
    @Transactional
    public void createTeam(TeamCreateRequestDto request, Long creatorUserId) {

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //팀 프로필사진 등록
        String imageUrl = null;
        if (request.getProjectImage() != null && !request.getProjectImage().isEmpty()) {
            log.info("팀 프로필 로직 시작---- vpn 확인하세요");

            try {
                //파일명 생성
                String originalFileName = request.getProjectImage().getOriginalFilename();
                String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_"); // 한글, 특수문자 제거
                String fileName = "teams/profile" + System.currentTimeMillis() + "_" + sanitizedFileName;

                // MinIO에 파일 업로드
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(profileBucket) // 팀 프로필용 버킷
                                .object(fileName)
                                .stream(request.getProjectImage().getInputStream(), request.getProjectImage().getSize(), -1)
                                .contentType(request.getProjectImage().getContentType())
                                .build()
                );

                imageUrl = minioUrl + "/" + profileBucket + "/" + fileName;
                log.info("팀 프로필 이미지 경로 확인: {}", imageUrl);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + "문제가 발생했습니다.");
            }

        }


        Team team = new Team(
                request.getProjectName(),
                encodedPassword,
                imageUrl
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


        //초대 링크 발송 - 여기 폼데이터일떄 기준으로 수정하기
        for (String email : request.getEmails()) {
            String inviteToken = UUID.randomUUID().toString();
            TeamInvite invite = new TeamInvite(inviteToken, team);
            teamInviteRepository.save(invite);

            //추후에 배포할때는 https://yourdomain.com/invite/{inviteToken} 여기로 수정
//            String inviteLink = "http://sqace.site/invite/" + inviteToken;
            String inviteLink = "http://localhost:3000/invite/" + inviteToken;
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
            log.error("팀 비밀번호: {}", team.getTeamPassword());
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

    //팀 마이페이지 정보 조회
    public TeamManagementResponseDto getTeamManagementInfo(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()->new IllegalArgumentException("팀 정보 없음"));

        //팀 멤버 정보 가져오기
        List<TeamMember> teamMembers = teamMemberRepository.findByTeam_TeamId(teamId);

         List<ManagementInfoDto> managementInfoDtos = teamMembers.stream()
                .map(member -> {
                    //feign호출
                    UserInfoDto userInfo = userServiceClient.getUserInfo(member.getUserId());

                    return new ManagementInfoDto(
                            member.getUserId(),
                            userInfo.getUserName(),
                            userInfo.getContactEmail(),
                            userInfo.getPhoneNumber(),
                            userInfo.getProfileImage(),
                            member.getRole()
                    );
                })
                .collect(Collectors.toList());

        return new TeamManagementResponseDto(
                team.getProjectName(),
                managementInfoDtos
        );
    }

    public void updateTeamName(Long teamId, String newTeamName, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 정보가 없습니다."));

        TeamMember teamMember = teamMemberRepository.findByTeamAndUserId(team, userId);

        if (teamMember.getRole() != TeamMember.Role.MASTER && teamMember.getRole() != TeamMember.Role.ADMIN) {
            throw new IllegalArgumentException("팀 이름을 변경할 권한이 없습니다.");
        }

        UpdateTeamNameRequestDto requestDto = new UpdateTeamNameRequestDto(teamId, newTeamName);
        try {
            chatServiceClient.updateTeamName(requestDto);
        } catch (Exception e) {
            throw new IllegalArgumentException("채팅 서비스에 팀 이름 전달중 오류발생");
        }

        team.setProjectName(newTeamName);
        teamRepository.save(team);
    }

    public void grantAdminRole(Long teamId, Long targetUserId, Long requestUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 정보가 없습니다."));
        TeamMember requester = teamMemberRepository.findByTeamAndUserId(team, requestUserId);

        if (requester.getRole() != TeamMember.Role.MASTER) {
            throw new IllegalArgumentException("ADMIN 권한을 부여할 수 있는 권한이 없습니다.");
        }

        TeamMember targetMember = teamMemberRepository.findByTeamAndUserId(team, targetUserId);

        if (targetMember.getRole() == TeamMember.Role.ADMIN) {
            throw new IllegalArgumentException("해당 회원은 이미 ADMIN 권한입니다.");
        }

        if (targetMember.getRole() == TeamMember.Role.MASTER) {
            throw new IllegalArgumentException("MASTER 권한은 변경이 불가능합니다.");
        }

        targetMember.setRole(TeamMember.Role.ADMIN);
        teamMemberRepository.save(targetMember);
    }

    //팀원 추가 초대
    public void grantMemberRole(Long teamId, Long targetUserId, Long requestUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 정보가 없습니다."));
        TeamMember requester = teamMemberRepository.findByTeamAndUserId(team, requestUserId);

        if (requester.getRole() != TeamMember.Role.MASTER) {
            throw new IllegalArgumentException("Member 권한을 부여할 수 있는 권한이 없습니다.");
        }

        TeamMember targetMember = teamMemberRepository.findByTeamAndUserId(team, targetUserId);

        if (targetMember.getRole() == TeamMember.Role.MEMBER) {
            throw new IllegalArgumentException("해당 회원은 이미 MEMBER 권한입니다.");
        }

        if (targetMember.getRole() == TeamMember.Role.MASTER) {
            throw new IllegalArgumentException("MASTER 권한은 변경이 불가능합니다.");
        }

        targetMember.setRole(TeamMember.Role.MEMBER);
        teamMemberRepository.save(targetMember);
    }

    public void addTeamMembers(Long teamId, Long requestUserId, List<String> emails) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 정보가 없습니다."));
        TeamMember requester = teamMemberRepository.findByTeamAndUserId(team, requestUserId);

        if (requester.getRole() != TeamMember.Role.MASTER && requester.getRole() != TeamMember.Role.ADMIN) {
            throw new IllegalArgumentException("팀원 초대 권한이 없습니다.");
        }
        //초대 토큰 가져오기
        TeamInvite existingInvite = teamInviteRepository.findByTeam(team);

        // 초대 토큰 만료 시간 확인 및 갱신
        if (existingInvite.getExpirationTime().isBefore(LocalDateTime.now())) {
            String newInviteToken = UUID.randomUUID().toString();
            existingInvite = new TeamInvite(newInviteToken, team); // 새로운 초대 토큰 객체 생성
            teamInviteRepository.save(existingInvite); // 새 초대 토큰 저장
            log.info("초대 토큰 갱신 완료. 새로운 초대 토큰: {}", newInviteToken);
        }

        String inviteToken = existingInvite.getInviteToken();

        //초대 링크 발송
        for (String email : emails) {
            //배포시 도메인으로 변경
            String inviteLink = "http://localhost:3000/invite/" + inviteToken;
            emailService.sendEmail(email, inviteLink);
            log.info("추가 초대 이메일 발송 완료: {}, 초대 링크: {}", email, inviteLink);
        }
        log.info("팀 ID {}에 대한 추가 초대 완료.", teamId);

    }

    public void removeTeamMember(Long teamId, Long targetUserId, Long requestUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 정보를 찾을 수 없습니다."));

        TeamMember requester = teamMemberRepository.findByTeamAndUserId(team, requestUserId);
        if (requester.getRole() != TeamMember.Role.MASTER) {
            throw new IllegalArgumentException("팀원 추방 권한이 없습니다.");
        }

        TeamMember targetMember = teamMemberRepository.findByTeamAndUserId(team, targetUserId);
        if (targetMember == null) {
            throw new IllegalArgumentException("대상 팀원을 찾을 수 없습니다.");
        }

        if (targetMember.getRole() == TeamMember.Role.MASTER) {
            throw new IllegalArgumentException("MASTER 권한의 팀원은 추방이 불가능합니다.");
        }

        teamMemberRepository.delete(targetMember);

        //페인으로 팀캘린더, 팀 채팅방 제거하는 로직 필요
        ChatParticipantDeleteRequestDto requestDto = new ChatParticipantDeleteRequestDto(teamId, targetUserId);
        chatServiceClient.deleteParticipant(requestDto);

        log.info("팀원 추방완료 - teamId: {}, targetUserId: {}", teamId, targetUserId);
    }

    //팀 메인페이지 정보 반환
    public TeamMainPageResponseDto getMainPage(Long teamId, Long userId) {

        //팀원 접속 상태
        List<TeamMember> teamMembers = teamMemberRepository.findByTeam_TeamId(teamId)
                .stream()
                .filter(member -> !member.getUserId().equals(userId))
                .collect(Collectors.toList());


        List<MemberInfoDto> members = teamMembers.stream()
                .map(member -> {
                    //Feign호출
                    UserDetailResponseDto userDetail = userServiceClient.getUserDetail(member.getUserId());
                    String userName = userDetail.getUserName();
                    String profileImage = userDetail.getProfileImage();

                    String userStatus = userStatusManager.getStatus(member.getUserId());

                    return new MemberInfoDto(member.getUserId(), userName, profileImage, userStatus);
                })
                .collect(Collectors.toList());

        //notice
        List<Notice> noticeEntity = noticeRepository.findAllByOrderByCreatedDateDesc();

        List<NoticeInfoDto> notices = noticeEntity.stream()
                .map(notice -> {
                    NoticeInfoDto dto = new NoticeInfoDto();
                    dto.setNoticeId(notice.getNoticeId());
                    dto.setNoticeTitle(notice.getTitle());
                    dto.setUserName(notice.getUserName());
                    dto.setCreatedDate(notice.getCreatedDate());
                    return dto;
                })
                .collect(Collectors.toList());

        //캘린더
        List<CalendarInfoDto> calendars;
        try {
            calendars = calendarServiceClient.getTeamEvents(teamId);
            log.info("캘린더 페인 호출");
        } catch (Exception e) {
            // 캘린더 데이터가 없을 경우 빈 리스트로 초기화
            log.warn("캘린더 데이터를 가져오는 중 문제가 발생했습니다. teamId: {}, error: {}", teamId, e.getMessage());
            calendars = new ArrayList<>();
        }

        //ResponseDto 생성
        TeamMainPageResponseDto responseDto = new TeamMainPageResponseDto();
        responseDto.setMembers(members);
        responseDto.setNotices(notices);
        responseDto.setCalendars(calendars);

        return responseDto;
    }
}
