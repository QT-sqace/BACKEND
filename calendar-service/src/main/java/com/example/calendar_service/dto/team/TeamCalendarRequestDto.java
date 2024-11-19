package com.example.calendar_service.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamCalendarRequestDto {
    //teamId를 페인클라이언트로 요청받는 dto

    private Long teamId;
}
