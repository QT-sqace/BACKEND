package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatParticipantDeleteRequestDto {

    private Long teamId;
    private Long userId;
}
