package com.example.chat_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    //채팅방 조회 반환Dto

    private Long roomId;
    private String roomName;
    private String lastMessage;
    private Long unreadCount;
    private String roomImage;

    //채팅리스트 갱신에 사용
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
