package com.example.chat_service.dto.request;

import com.example.chat_service.entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
public class ChatMessageDto {
    //채팅 요청 dto

    private Long roomId;
    private Long senderId;
    private String senderName;
    private String senderProfileImage;
    private String content;
    private ChatMessage.MessageType messageType;
    private String sendTime;
}
