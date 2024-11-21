package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberInfoDto {

    private Long userId;
    private String role;
}
