package com.example.team_service.dto.request.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamInviteRequestDto {

    @Email(message = "유효한 이메일 주소여야 합니다.")
    @NotNull(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "팀 ID는 필수입니다.")
    private Long teamId;
}
