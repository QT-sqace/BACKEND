package com.example.team_service.repository;

import com.example.team_service.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 특정 작성자(TeamMember) ID로 공지사항 조회
    List<Notice> findByCreatedByTeamMemberId(Long teamMemberId);
}
