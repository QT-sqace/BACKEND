package com.example.team_service.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequestDto {
    private String projectName;
    private String password;
    private String confirmPassword;
    private String projectImage;
    
}

