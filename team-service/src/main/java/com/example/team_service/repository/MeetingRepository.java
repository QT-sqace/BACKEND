package com.example.team_service.repository;

import com.example.team_service.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    // 팀 ID로 미팅룸 목록 조회
    List<Meeting> findByTeam_TeamId(Long teamId);
}
