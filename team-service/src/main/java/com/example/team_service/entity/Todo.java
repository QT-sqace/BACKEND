package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

@Entity
@Table(name = "todos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false)
    private Long userId; // 유저 ID 저장

    @Column(nullable = true)
    private String userName; // 유저 이름 저장 (Optional)

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}


