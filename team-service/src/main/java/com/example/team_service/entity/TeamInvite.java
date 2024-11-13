package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class TeamInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invite_id;

    @Column(nullable = false, unique = true)
    private String inviteToken; // 초대 토큰

    @Column(nullable = false)
    private LocalDateTime expiration_time; // 만료 시간

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team; // 초대된 팀 참조

    public TeamInvite(String inviteToken, LocalDateTime expiration_time, Team team) {
        this.inviteToken = inviteToken;
        this.expiration_time = expiration_time;
        this.team = team;
    }
}
