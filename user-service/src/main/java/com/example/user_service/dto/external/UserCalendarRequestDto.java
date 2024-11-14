package com.example.user_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//캘린더 서비스에 userId 전달하는 dto
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCalendarRequestDto {

    private Long userId;

}
