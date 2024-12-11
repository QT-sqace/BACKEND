package com.example.user_service.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UpdateProfileRequestDto {

    private String userName;
    private String contactEmail;
    private String address;
    private String phoneNumber;
    private String company;
    private MultipartFile profileImage;
}
