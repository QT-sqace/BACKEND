package com.example.team_service.repository;

import com.example.team_service.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // 특정 팀의 멤버 전체 조회
    List<TeamMember> findAllByTeamTeamId(Long teamId);

    // 특정 유저의 모든 멤버십 조회
    List<TeamMember> findAllByUserId(Long userId);

}

