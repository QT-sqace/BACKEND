package com.example.team_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberRemoveRequestDto {

    private Long teamId;
    private Long userId;
}
