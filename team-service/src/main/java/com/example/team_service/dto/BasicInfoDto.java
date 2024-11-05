package com.example.team_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicInfoDto {
    //유저 정보를 받기 위한 dto
    private String userName;
    private String email;
}
