package com.example.user_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    private String userName;
    private String contactEmail;
    private String phoneNumber;
    private String profileImage;
}
