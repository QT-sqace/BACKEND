package com.example.chat_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamInfoDto {
    //팀원들 접속상태 반환

    private Long teamId;
    private String teamName;
    private List<TeamMemberDto> members;
}
