package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTeamNameRequestDto {

    private Long teamId;
    private String teamName;
}
