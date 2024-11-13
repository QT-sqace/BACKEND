package com.example.user_service.jira.service;

public interface JiraAuthService {
    String requestAccessToken(String code);
    String getStoredAccessToken();
    String getUserInfo(String accessToken) throws Exception;
}