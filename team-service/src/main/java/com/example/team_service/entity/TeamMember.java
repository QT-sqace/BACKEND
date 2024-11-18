package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMemberId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public TeamMember(Team team, Long userId, String role, LocalDateTime joinedAt) {
        this.team = team;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}