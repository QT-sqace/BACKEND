package com.example.chat_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserProfileDto {

    private Long userId;
    private String userName;
    private String profileImage;
}
