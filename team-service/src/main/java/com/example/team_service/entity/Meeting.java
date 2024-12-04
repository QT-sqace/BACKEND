package com.example.team_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "team_meeting")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false) // Team 테이블의 teamId와 매핑
    private Team team;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false) // TeamMember 테이블의 teamMemberId와 매핑
    private TeamMember createdBy;

    @Column(nullable = false)
    private String meetingName; // 방 이름

    @Column(nullable = false, updatable = false)
    private LocalDateTime meetingDate; // 미팅 생성일

    private String meetingUrl; // 미팅 주소 (nullable)

    @Column(name = "user_name")
    private String userName; // 작성자 이름 (프론트에서 설정)

    // 생성자
    public Meeting(Team team, TeamMember createdBy, String meetingName, String meetingUrl) {
        this.team = team;
        this.createdBy = createdBy;
        this.meetingName = meetingName;
        this.meetingUrl = meetingUrl;
    }

    // 생성 시 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        this.meetingDate = LocalDateTime.now().withNano(0); // 밀리초 제거
    }
}
