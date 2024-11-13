package com.example.user_service.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//회원가입 요청 dto
@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

}
