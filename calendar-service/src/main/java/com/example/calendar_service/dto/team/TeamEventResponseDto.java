package com.example.calendar_service.dto.team;

import com.example.calendar_service.entity.CalendarInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamEventResponseDto {
    private Long eventId;
    private String title;
    private String content;
    private Boolean allDay;
    private String startDate;
    private String endDate;
    private String color;
    private CalendarInfo.EventType eventType;
}
