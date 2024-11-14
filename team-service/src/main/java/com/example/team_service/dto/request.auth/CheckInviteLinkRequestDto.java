package com.example.team_service.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInviteLinkRequestDto {

    @NotBlank(message = "초대 링크 토큰은 필수입니다.")
    private String inviteToken;
}
