package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamInviteResponseDto {

    private String message;
    private String inviteLink;
}
