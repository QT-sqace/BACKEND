package com.example.chat_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class ChatParticipant {
    //채팅 참여자 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;  //Room  엔티티와 매핑

    @Column
    private Long userId;

    @Column
    private String userName;

    @Column
    private String profileImage;    //사용자 이미지 경로

    @Column(name = "join_date", nullable = false, updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime joinDate;

    @Column
    private Long lastReadMessageId; //마지막으로 읽은 메시지 ID

    public ChatParticipant(ChatRoom room, Long userId, String userName, String profileImage, LocalDateTime joinDate) {
        this.room = room;
        this.userId = userId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.joinDate = joinDate;
        this.lastReadMessageId = 0L;
    }

    public void setLastReadMessageId(Long messageId) {
        this.lastReadMessageId = messageId;
    }
}
