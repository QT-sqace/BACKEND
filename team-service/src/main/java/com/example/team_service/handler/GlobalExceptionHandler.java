package com.example.team_service.handler;

import com.example.team_service.dto.BasicResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    //공통 예외처리 핸들러

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BasicResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BasicResponseDto.failure(ex.getMessage(), null));
    }

    // IllegalStateException 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BasicResponseDto> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden 상태 반환
                .body(BasicResponseDto.failure(ex.getMessage(), null)); // 예외 메시지 반환
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BasicResponseDto> handleNullPointerException(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BasicResponseDto.failure("Null 값 참조로 인해 오류가 발생했습니다.", null));
    }

    // Generic Exception 처리 (모든 예외를 처리하는 최상위 핸들러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BasicResponseDto> handleAllException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BasicResponseDto.failure("요청 처리 중 오류가 발생했습니다.", null));
    }
}
