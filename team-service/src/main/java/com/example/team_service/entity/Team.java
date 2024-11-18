package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false, unique = true)
    private String projectName;

    @Column(nullable = false)
    private String password;

    private String projectImage;

    @Column(name = "admin_user_id", nullable = false) // 관리자의 user_id 컬럼 추가
    private Long adminUserId;

    // TeamMember와의 일대다 관계 설정
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> members;

    public Team(String project_name, String password, String project_image, Long admin_user_id) {
        this.projectName = project_name;
        this.password = password;
        this.projectImage = project_image;
        this.adminUserId = admin_user_id;
    }
}