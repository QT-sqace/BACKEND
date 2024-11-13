package com.example.user_service.dto.response.auth;

import com.example.user_service.common.ResponseCode;
import com.example.user_service.common.ResponseMessage;
import com.example.user_service.dto.response.ResponseDto;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

//로그인 반환 dto
@Getter
public class SignInResponseDto extends ResponseDto {

    private String accessToken;
    private int expirationTime;
    private Long userId;
    private String email;
    private String userName;
    private String address;
    private String phoneNumber;
    private String contactEmail;
    private String company;
    private String profileImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    private SignInResponseDto(String accessToken) {
        super();
        this.accessToken = accessToken;
        //초단위 60초 * 60분 * 12 시간
        this.expirationTime = 43200;
    }

    private SignInResponseDto(String accessToken,Long userId, String email, String userName, String address, String phoneNumber, String contactEmail, String company, String profileImage, LocalDateTime createdAt) {
        super();
        this.accessToken = accessToken;
        this.expirationTime = 43200;
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactEmail = contactEmail;
        this.company = company;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    // 로그인 성공시
    public static ResponseEntity<SignInResponseDto> success(String accessToken, User user, UserInfo userinfo) {
        SignInResponseDto responseBody = new SignInResponseDto(
                accessToken,
                user.getUserId(),
                user.getEmail(),
                userinfo.getUserName(),
                userinfo.getAddress(),
                userinfo.getPhoneNumber(),
                userinfo.getContactEmail(),
                userinfo.getCompany(),
                userinfo.getProfileImage(),
                userinfo.getCreatedAt()
                );
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    //실패시
    public static ResponseEntity<ResponseDto> signInFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SIGN_IN_FAIL, ResponseMessage.SIGN_IN_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
