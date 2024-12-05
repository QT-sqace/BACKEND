package com.example.calendar_service.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamEventUpdateRequestDto {
    //팀 일정 수정 dto
    private Long teamId;
    private String title;
    private String content;
    private String startDate;
    private String endDate;
    private Boolean allDay;
    private String color;
}
