package com.example.user_service.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponseDto {

    private String profileImage;
    private String userName;
    private String contactEmail;
    private String address;
    private String phoneNumber;
    private String company;
    private String provider;
}
