package com.example.user_service.jira.controller;

import com.example.user_service.jira.service.JiraAuthService;
import com.example.user_service.jira.config.JiraConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@RestController
@RequestMapping("/jira/auth")
public class JiraAuthController {

    private final JiraAuthService jiraAuthService;
    private final JiraConfig jiraConfig;

    public JiraAuthController(JiraAuthService jiraAuthService, JiraConfig jiraConfig) {
        this.jiraAuthService = jiraAuthService;
        this.jiraConfig = jiraConfig;
    }

    // Step 1: Jira 인증 페이지로 리디렉션하는 /login 엔드포인트 추가
    @GetMapping("/login")
    public ResponseEntity<String> redirectToJiraAuth() {
        String authUrl = "https://auth.atlassian.com/authorize" +
                "?audience=api.atlassian.com" +
                "&client_id=" + jiraConfig.getClientId() +
                "&scope=read%3Ame%20read%3Aaccount%20read%3Ajira-user" +  // 필요한 스코프 추가
                "&redirect_uri=" + jiraConfig.getRedirectUri() +
                "&state=" + UUID.randomUUID().toString() +  // 무작위 상태 값
                "&response_type=code" +
                "&prompt=none";

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", authUrl).build();
    }

    // Step 2: 인증 코드로 액세스 토큰을 요청하는 /callback 엔드포인트
    @GetMapping("/callback")
    public ResponseEntity<String> handleJiraCallback(@RequestParam("code") String code) {
        try {
            String accessToken = jiraAuthService.requestAccessToken(code);
            // 액세스 토큰을 성공적으로 받아온 후 임시 경로로 리다이렉트
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/jira/auth/success")
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve access token: " + e.getMessage());
        }
    }

    // (선택) Step 3: 액세스 토큰으로 사용자 정보를 가져오는 /user-info 엔드포인트
    @GetMapping("/user-info")
    public ResponseEntity<String> getUserInfo() {
        try {
            String accessToken = jiraAuthService.getStoredAccessToken(); // 토큰 저장소에서 가져오기
            String userInfo = jiraAuthService.getUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve user info: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> authSuccess() {
        return ResponseEntity.ok("Jira authentication was successful!");
    }
}