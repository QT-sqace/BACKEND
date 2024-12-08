package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//캘린더 서비스에 teamId 전달하는 dto
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamCalendarRequestDto {

    private Long teamId;
}
