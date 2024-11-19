package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamMemberId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 역할을 Enum으로 선언
    @Enumerated(EnumType.STRING) // Enum을 String으로 저장
    @Column(nullable = false)
    private Role role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public TeamMember(Team team, Long userId, Role role, LocalDateTime joinedAt) {
        this.team = team;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    // Enum 정의
    public enum Role {
        MASTER, //팀 생성자
        ADMIN,  // 관리자
        MEMBER  // 일반 멤버
    }
}
