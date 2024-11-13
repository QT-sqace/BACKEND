package com.example.user_service.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//인증번호 요청 dto
@Getter
@Setter
@NoArgsConstructor
public class EmailCertificationRequestDto {

    @Email
    @NotBlank
    private String email;
}
