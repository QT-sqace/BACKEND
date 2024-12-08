package com.example.team_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinTeamRequestDto {
    private String inviteToken; //초대 토큰
    private String password;    //팀 비밀번호
}
