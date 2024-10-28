package com.example.user_service.dto.response.auth;

import com.example.user_service.common.ResponseCode;
import com.example.user_service.common.ResponseMessage;
import com.example.user_service.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//로그인 반환 dto
@Getter
public class SignInResponseDto extends ResponseDto {

    private String accessToken;
    private int expirationTime;


    private SignInResponseDto(String accessToken) {
        super();
        this.accessToken = accessToken;
        this.expirationTime = 3600;
    }

    //성공시
    public static ResponseEntity<SignInResponseDto> success(String accessToken) {
        SignInResponseDto responseBody = new SignInResponseDto(accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    //실패시
    public static ResponseEntity<ResponseDto> signInFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SIGN_IN_FAIL, ResponseMessage.SIGN_IN_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
