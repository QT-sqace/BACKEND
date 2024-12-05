package com.example.chat_service.dto.response;

import com.example.chat_service.dto.request.ChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDataResponseDto {
    //메시지 반환 Dto

    private List<ChatMessageDto> messages;
    private List<ChatParticipantDto> participants;
}
