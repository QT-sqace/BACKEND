package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestDto {
    //테스트 용도로 프론트에서 팀생성시 팀이름, 패스워드 보낸다 가정
    private String teamName;
    private String password;
}
