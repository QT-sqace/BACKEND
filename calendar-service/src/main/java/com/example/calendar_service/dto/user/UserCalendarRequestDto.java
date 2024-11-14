package com.example.calendar_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCalendarRequestDto {
    //페인클라이언트로 요청 받는 dto

    private Long userId;
}
