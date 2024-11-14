package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String project_name;

    @Column(name = "team_password", nullable = false) // 컬럼명 수정
    private String password;

    @Column
    private String project_image;

    @Column(nullable = false)
    private Long creator_user_id;

    public Team(String project_name, String password, String project_image, Long creator_user_id) {
        this.project_name = project_name;
        this.password = password;
        this.project_image = project_image;
        this.creator_user_id = creator_user_id;
    }
}
