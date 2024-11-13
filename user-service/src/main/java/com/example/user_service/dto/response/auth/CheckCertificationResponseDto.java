package com.example.user_service.dto.response.auth;

import com.example.user_service.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//인증번호 확인 반환dto
@Getter
public class CheckCertificationResponseDto extends ResponseDto {

    private CheckCertificationResponseDto () {
        super();
    }

    public static ResponseEntity<CheckCertificationResponseDto> success() {
        CheckCertificationResponseDto responseBody = new CheckCertificationResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    //실패시 반환
    public static ResponseEntity<ResponseDto> certificationFail() {
        ResponseDto responseBody = new ResponseDto("Fail", "certification Error");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
