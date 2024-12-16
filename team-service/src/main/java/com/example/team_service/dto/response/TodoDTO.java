package com.example.team_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoDTO {
    private Long todoId;
    private String title;
    private String description;
    private Boolean completed;
    private String userName;
    private String userProfileImage;
}