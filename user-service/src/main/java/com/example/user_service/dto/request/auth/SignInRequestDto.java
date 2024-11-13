package com.example.user_service.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//회원 이메일 로그인 dto
@Getter
@Setter
@NoArgsConstructor
public class SignInRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
