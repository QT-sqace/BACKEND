package com.example.jiralink.dto;

import lombok.Data;

@Data
public class JiraLinkRequest {
    private String jiraProjectId;
    private String jiraProjectName;
}