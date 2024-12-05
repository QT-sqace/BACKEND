package com.example.chat_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    @Column
    private String fileName;

    @Column
    private String filePath; // 파일 경로 (예: uploads/filename.pdf)

    @Column
    private int fileSize; // 파일 크기 (KB 단위)

    @Column
    private Long uploadedBy; // 파일 업로드 사용자 ID

    @Column
    private LocalDateTime uploadDate;   //보낸 날짜
}
