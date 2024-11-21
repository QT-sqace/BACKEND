package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberResponseDto {
    //캘린더 서비스에 반환하는 dto

    private Long teamId;
    private List<TeamMemberInfoDto> members; //팀 멤버 리스트 - userId, role
}
