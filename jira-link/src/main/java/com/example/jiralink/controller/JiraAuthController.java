package com.example.jiralink.controller;

import com.example.jiralink.config.JiraConfig;
import com.example.jiralink.service.JiraAuthService;
import com.example.jiralink.util.JwtUtil;
import com.example.jiralink.util.ScopeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jira/auth")
public class JiraAuthController {

    private final ScopeManager scopeManager;
    private final JiraAuthService jiraAuthService;
    private final JiraConfig jiraConfig;
    private final JwtUtil jwtUtil;

    // Step 1: Jira 인증 페이지로 리디렉션하는 /login 엔드포인트 추가
    @GetMapping("/login")
    public ResponseEntity<String> redirectToJiraAuth(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        // JwtUtil을 사용하여 Authorization 헤더에서 userId 추출
        Long userId;
//        try {
//            userId = jwtUtil.extractedUserIdFromHeader(authorizationHeader);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Invalid Authorization header: " + e.getMessage());
//        }

        //테스트용!!!!!!!! 배포시 삭제
        if (authorizationHeader != null) {
            // JWT에서 userId 추출
            userId = jwtUtil.extractedUserIdFromHeader(authorizationHeader);
        } else {
            // 테스트용 userId (브라우저에서 헤더가 없는 경우)
            userId = 1L; // 임시 userId
        }

        String scopes = scopeManager.getScopeString();
        String state = userId + ":" + UUID.randomUUID();

        String authUrl = "https://auth.atlassian.com/authorize" +
                "?audience=api.atlassian.com" +
                "&client_id=" + jiraConfig.getClientId() +
                "&scope=" + scopes +  // 필요한 스코프 추가
                "&redirect_uri=" + jiraConfig.getRedirectUri() +
                "&state=" + state +  // 무작위 상태 값
                "&response_type=code" +
                "&prompt=none"; //로그인 상태 유지

        return ResponseEntity.status(HttpStatus.FOUND).header("Location", authUrl).build();
    }

    // Step 2: 인증 코드로 액세스 토큰을 요청하는 /callback 엔드포인트
    @GetMapping("/callback")
    public ResponseEntity<String> handleJiraCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            String[] stateParts = state.split(":");
            Long userId = Long.valueOf(stateParts[0]);

            String accessToken = jiraAuthService.requestAccessToken(userId, code);
            String cloudId = jiraAuthService.fetchCloudId(accessToken);

            jiraAuthService.updateAccessTokenAndCloudId(userId, accessToken, cloudId);

            return ResponseEntity.ok("Jira integration successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to complete Jira integration: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> authSuccess() {
        return ResponseEntity.ok("Jira authentication was successful!");
    }
}