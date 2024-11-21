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
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath; // 파일 경로 (예: uploads/filename.pdf)

    @Column(nullable = false)
    private int fileSize; // 파일 크기 (KB 단위)

    @Column(nullable = false)
    private Long uploadedBy; // 파일 업로드 사용자 ID

    @Column(nullable = false)
    private LocalDateTime uploadDate;
}

