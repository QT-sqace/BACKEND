package com.example.team_service.repository;

import com.example.team_service.entity.Team;
import com.example.team_service.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    boolean existsByTeamAndUserId(Team team, Long userId);
}
