package com.example.chat_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @Column
    private Long roomMessageId; //방별 메시지ID - 읽지않는 개수 세는용도

    @Column
    private Long senderId;    //메시지 전송자 ID

    @Enumerated(EnumType.STRING)
    @Column
    private MessageType messageType;    //TEXT, FILE, IMAGE

    @Column(name = "send_time", nullable = false, updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime sendTime;

    @Column(columnDefinition = "JSON")
    private String content;


    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatFile> files = new ArrayList<>();

    public ChatMessage(ChatRoom room, Long roomMessageId, Long senderId, String content, LocalDateTime sendTime, MessageType messageType) {
        this.room = room;
        this.roomMessageId = roomMessageId;
        this.senderId = senderId;
        this.content = content;
        this.sendTime = sendTime;
        this.messageType = messageType;
    }



    public enum MessageType {
        TEXT, FILE, IMAGE
    }
}
