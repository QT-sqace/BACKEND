package com.example.calendar_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarInfoDto {

    private Long eventId;
    private String title;
    private String startDate;
    private String endDate;
    private String color;
}
