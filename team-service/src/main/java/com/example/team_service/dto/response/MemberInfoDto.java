package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {

    private Long userId;
    private String userName;
    private String userImage;
    private String userStatus;
}
