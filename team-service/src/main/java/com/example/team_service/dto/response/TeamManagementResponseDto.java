package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeamManagementResponseDto {
    //팀관리 페이지 반환 dto

    private String teamName;
//    private String teamPassword;  //팀 비번은 사용 안함
    private List<ManagementInfoDto> teamMembers;
}
