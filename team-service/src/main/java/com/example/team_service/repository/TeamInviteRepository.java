package com.example.team_service.repository;

import com.example.team_service.entity.TeamInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    Optional<TeamInvite> findByInviteToken(String inviteToken); // inviteToken으로 초대 조회

    // 초대 링크가 만료되지 않은 경우에만 조회
    Optional<TeamInvite> findByInviteTokenAndExpirationTimeAfter(String inviteToken, LocalDateTime currentDate);
}
