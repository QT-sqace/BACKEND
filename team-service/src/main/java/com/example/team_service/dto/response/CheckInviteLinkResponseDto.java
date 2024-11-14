package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInviteLinkResponseDto {

    private String message;  // 응답 메시지
    private boolean valid;   // 초대 링크의 유효성 여부
}
