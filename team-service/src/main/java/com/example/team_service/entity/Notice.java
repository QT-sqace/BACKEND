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
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(nullable = false)
    private String title; // 공지 제목

    @Lob
    @Column(nullable = false)
    private String content; // 공지 내용

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false) // TeamMember 테이블의 teamMemberId와 매핑
    private TeamMember createdBy;

    @Column(name = "user_name")
    private String userName; // 작성자 이름 (프론트에서 설정)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate; // 작성 날짜

    private LocalDateTime updatedDate; // 수정 날짜

    // 생성자
    public Notice(String title, String content, TeamMember createdBy) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
    }

    // 생성 시 날짜 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now().withNano(0); // 밀리초 제거
    }

    // 수정 시 날짜 자동 업데이트
    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now().withNano(0); // 밀리초 제거
    }
}
