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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "user_id", nullable = false)
    private Long user_id;

    @Column(nullable = false)
    private String role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joined_at;

    public TeamMember(Team team, Long user_id, String role, LocalDateTime joined_at) {
        this.team = team;
        this.user_id = user_id;
        this.role = role;
        this.joined_at = joined_at;
    }
}