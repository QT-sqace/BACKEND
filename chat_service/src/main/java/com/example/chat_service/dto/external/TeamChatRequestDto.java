package com.example.chat_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamChatRequestDto {
    //팀생성 시점에 팀 서비스로 부터 받는 팀챗룸 생성 dto
    private Long teamId;
    private String roomName;    //팀 생성때 사용 이름
    private String roomImage;   //팀 이미지 경로
    private Long creatorUserId; //팀 생성자 ID
    private String userName;    //팀 생성자 닉네임
    private String creatorProfileImage; //팀 생성자의 프로필 이미지 경로
}
