package com.example.calendar_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicResponseDto<T> {

    private String code;    // 성공: "Success", 실패: "Fail"
    private String message; // 응답 메시지
    private T data;         // 응답 데이터

    public static <T> BasicResponseDto<T> success(String message, T data) {
        return new BasicResponseDto<>("Success", message, data);
    }

    public static <T> BasicResponseDto<T> failure(String message, T data) {
        return new BasicResponseDto<>("Fail", message, data);
    }
}
