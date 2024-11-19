package com.example.calendar_service.handler;

import com.example.calendar_service.dto.BasicResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    //우선은 모든 예외를 처리하는 기본 핸들러

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponseDto> handleAllException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BasicResponseDto.failure("요청 처리 중 오류가 발생했습니다.", null));
    }
}
