package com.example.team_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequestDto {
    private String projectName;        //워크스페이스 이름
    private String password;            //팀 비밀번호
    private String projectImage;       //팀 프로필
}