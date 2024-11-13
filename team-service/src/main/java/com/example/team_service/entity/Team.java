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
    private Long team_id;

    @Column(nullable = false, unique = true)
    private String project_name;

    @Column(nullable = false)
    private String password;

    private String project_image;

    @Column(name = "admin_user_id", nullable = false) // 관리자의 user_id 컬럼 추가
    private Long admin_user_id;

    // TeamMember와의 일대다 관계 설정
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> members;

    public Team(String project_name, String password, String project_image, Long admin_user_id) {
        this.project_name = project_name;
        this.password = password;
        this.project_image = project_image;
        this.admin_user_id = admin_user_id;
    }
}