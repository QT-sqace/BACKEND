package com.example.team_service.service;

import com.example.team_service.dto.response.NoticeDTO;
import com.example.team_service.entity.Notice;
import com.example.team_service.entity.TeamMember;
import com.example.team_service.repository.NoticeRepository;
import com.example.team_service.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final TeamMemberRepository teamMemberRepository;


    // 사용자(teamMemberId)가 해당 팀의 멤버인지 확인
    public boolean isUserTeamMember(Long userId, Long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버를 찾을 수 없습니다: " + teamMemberId));
        return teamMember.getUserId().equals(userId);
    }

    // 사용자가 공지사항의 작성자인지 확인
    public boolean isNoticeOwner(Long userId, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + noticeId));
        return notice.getCreatedBy().getUserId().equals(userId);
    }


    // 공지사항 생성
    public NoticeDTO createNotice(String title, String content, Long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버를 찾을 수 없습니다: " + teamMemberId));

        Notice notice = new Notice(title, content, teamMember);
        Notice savedNotice = noticeRepository.save(notice);

        return convertToDTO(savedNotice);
    }

    // 공지사항 수정
    public NoticeDTO updateNotice(Long noticeId, String newTitle, String newContent) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + noticeId));

        notice.setTitle(newTitle);
        notice.setContent(newContent);
        notice.setUpdatedDate(LocalDateTime.now());
        Notice updatedNotice = noticeRepository.save(notice);

        return convertToDTO(updatedNotice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + noticeId));
        noticeRepository.delete(notice);
    }

    // 공지사항 조회 (단건)
    public NoticeDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + noticeId));

        return convertToDTO(notice);
    }

    // 공지사항 목록 조회
    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 작성자별 공지사항 조회
    public List<NoticeDTO> getNoticesByAuthor(Long teamMemberId) {
        List<Notice> notices = noticeRepository.findByCreatedByTeamMemberId(teamMemberId);
        return notices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Notice -> NoticeDTO 변환
    private NoticeDTO convertToDTO(Notice notice) {
        return NoticeDTO.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdDate(notice.getCreatedDate())
                .userId(notice.getCreatedBy().getUserId())
                .role(notice.getCreatedBy().getRole().name())
                .build();
    }
}
