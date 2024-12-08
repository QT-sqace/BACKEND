package com.example.calendar_service.dto.user;

import com.example.calendar_service.entity.CalendarInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserEventResponseDto {
    //개인이 일정 조회에 사용하는 dto

    private Long eventId;
    private String title;
    private String content;
    private Boolean allDay;
    private String startDate;
    private String endDate;
    private String color;
    private CalendarInfo.EventType eventType;
}
