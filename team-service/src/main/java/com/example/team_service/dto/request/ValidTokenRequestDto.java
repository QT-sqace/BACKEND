package com.example.team_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidTokenRequestDto {
    //inviteToken 요청 dto

    private String inviteToken; //초대 토큰
}
