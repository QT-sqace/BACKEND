package com.example.user_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDto {
    //팀 메인페이지 활용

    private String userName;
    private String profileImage;
}
