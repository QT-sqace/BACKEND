package com.example.chat_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberDto {
    //회원들의 접속상태 반환

    private Long userId;
    private String userName;
    private String userImage;
    private String userStatus;
}
