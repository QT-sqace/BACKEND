package com.example.team_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamCreateRequestDto {
    private String project_name;
    private String password;
    private String confirm_password;
    private String project_image;
}