package com.example.calendar_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberInfoDto {
    //TeamMemberResponseDto 반환 받을때 사용

    private Long userId;
    private String role;    //MASTER, ADMIN, MEMBER
}
