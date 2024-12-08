package com.example.calendar_service.handler;

import com.example.calendar_service.dto.BasicResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // IllegalStateException 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BasicResponseDto> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden 상태 반환
                .body(BasicResponseDto.failure(ex.getMessage(), null)); // 예외 메시지 반환
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BasicResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Request 상태 반환
                .body(BasicResponseDto.failure(ex.getMessage(), null)); // 예외 메시지 반환
    }

    // 모든 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponseDto> handleAllException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error 상태 반환
                .body(BasicResponseDto.failure("요청 처리 중 오류가 발생했습니다.", null)); // 기본 메시지 반환
    }
}
