package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CalendarInfoDto {

    private Long eventId;
    private String title;
    private String content;
    private Boolean allDay;
    private String startDate;
    private String endDate;
    private String color;
}
