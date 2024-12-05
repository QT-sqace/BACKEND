package com.example.chat_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatParticipantDto {

    private Long userId;
    private String userName;
    private String profileImage;
}
