package com.example.calendar_service.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamEventRequestDto {
    //팀 일정 요청 DTO
    private Long teamId;
    private String title;
    private String content;
    private Boolean allDay;
    private String startDate;
    private String endDate;
    private String color;
}
