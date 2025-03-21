package com.example.team_service.repository;

import com.example.team_service.entity.Team;
import com.example.team_service.entity.TeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    Optional<TeamInvite> findByInviteToken(String inviteToken);

    TeamInvite findByTeam(Team team);
}