package com.example.team_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(nullable = false)
    private String projectName; // 팀 이름

    @Column(nullable = false)
    private String teamPassword; // 팀 비밀번호

    private String projectImage; // 팀 이미지 경로

    // TeamMember와의 일대다 관계 설정
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 순환 참조 방지
    private Set<TeamMember> members = new HashSet<>();

    // 생성자
    public Team(String project_name, String password, String project_image) {
        this.projectName = project_name;
        this.teamPassword = password;
        this.projectImage = project_image;
    }
}
