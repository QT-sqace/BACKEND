package com.example.team_service.repository;

import com.example.team_service.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    //userId로 팀 멤버 조회
    List<TeamMember> findByUserId(Long userId);
}