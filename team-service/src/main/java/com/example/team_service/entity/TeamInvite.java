package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class TeamInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inviteId;

    @Column(nullable = false, unique = true)
    private String inviteToken; // 초대 토큰

    @Column(nullable = false)
    private LocalDateTime expirationTime; // 만료 시간

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team; // 초대된 팀 참조

    public TeamInvite(String inviteToken, Team team) {
        this.inviteToken = inviteToken;
        this.team = team;
        this.expirationTime = LocalDateTime.now().plusHours(24);    //토큰 만료시간 24시간
    }
}
