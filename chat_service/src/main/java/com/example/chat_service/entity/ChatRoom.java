package com.example.chat_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {
    //채팅방

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column
    private Long teamId;    //팀챗일 경우

    @Column
    private String roomName;    //채팅방 이름 <- 팀 이름

    @Column
    private String roomImage;   //채팅방 프로필 경로

    @Enumerated(EnumType.STRING)
    @Column
    private RoomState roomState;    //TEAM, PRIVATE

    @Column(name = "room_created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime roomCreatedAT;  //생성일

    //매핑 설정
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatParticipant> participants; // 채팅방 참여자 리스트

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatMessage> messages; // 채팅 메시지 리스트

    //팀 채팅방 생성시 사용
    public ChatRoom(Long teamId, String roomName, String roomImage, RoomState roomState) {
        this.teamId = teamId;
        this.roomName = roomName;
        this.roomImage = roomImage;
        this.roomState = roomState;
        this.roomCreatedAT = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }


    public enum RoomState {
        TEAM, PRIVATE
    }
}
