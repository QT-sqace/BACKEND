package com.example.team_service.dto.external;

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
    private LocalDateTime joinDate; //채팅방 참여 시간

    public ChatParticipantAddRequestDto(Long teamId, Long userId,String userName, String profileImage) {
        this.teamId = teamId;
        this.userId = userId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.joinDate = LocalDateTime.now().withSecond(0).withNano(0);
    }
}
