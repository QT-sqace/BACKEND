package com.example.chat_service.dto.external;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatParticipantAddRequestDto {
    //페인으로 챗 서비스에 팀원추가시 보내는 dto

    private Long teamId;
    private Long userId;
    private String userName;
    private String profileImage;    //회원 프로필 이미지
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinDate; //채팅방 참여 시간

}
