package com.example.calendar_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserEventRequestDto {

    //일정 등록 요청 Dto
    private String title;
    private String content;
    private Boolean allDay;
    private String startDate;
    private String endDate;
    private String color;
}
