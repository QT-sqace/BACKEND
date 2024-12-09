package com.example.jiralink.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "team_jira_mapping")
@Data
public class TeamJiraMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamJiraId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long teamId;

    private String jiraProjectId;

    private String jiraProjectName;

    @Column(updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}