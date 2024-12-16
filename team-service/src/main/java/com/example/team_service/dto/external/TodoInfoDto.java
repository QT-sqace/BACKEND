package com.example.team_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoInfoDto {
    private long userId;
    private String name;
}