package com.example.team_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicResponseDto<T> {
    //기본 응답 DTO
    private String code;    // 성공: "Success", 실패: "Fail"
    private String message; // 응답 메시지
    private T data;         // 응답 데이터   T: Generic 타입선언

    public static <T> BasicResponseDto<T> success(String message, T data) {
        return new BasicResponseDto<>("Success", message, data);
    }

    public static <T> BasicResponseDto<T> failure(String message, T data) {
        return new BasicResponseDto<>("Fail", message, data);
    }
}
