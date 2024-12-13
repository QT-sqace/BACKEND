package com.example.jiralink.controller;

import com.example.jiralink.config.JiraConfig;
import com.example.jiralink.service.JiraAuthService;
import com.example.jiralink.util.JwtUtil;
import com.example.jiralink.util.ScopeManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
    public RedirectView redirectToJiraAuth(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
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
                "&scope=" + scopes +
                "&redirect_uri=" + jiraConfig.getRedirectUri() +
                "&state=" + state +
                "&response_type=code" +
                "&prompt=none";

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(authUrl);
        return redirectView;
    }


    // Step 2: 인증 코드로 액세스 토큰을 요청하는 /callback 엔드포인트
    @GetMapping("/callback")
    public RedirectView handleJiraCallback(@RequestParam("code") String code, @RequestParam("state") String state) {
        try {
            String[] stateParts = state.split(":");
            Long userId = Long.valueOf(stateParts[0]);

            String accessToken = jiraAuthService.requestAccessToken(userId, code);
            String cloudId = jiraAuthService.fetchCloudId(accessToken);

            jiraAuthService.updateAccessTokenAndCloudId(userId, accessToken, cloudId);

            // 인증 완료 후 리다이렉션 URL 설정
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/success"); // 성공 페이지 또는 클라이언트로 리다이렉션
            return redirectView;

        } catch (Exception e) {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/error?message=" + e.getMessage()); // 에러 처리 페이지로 리다이렉션
            return redirectView;
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> authSuccess() {
        return ResponseEntity.ok("Jira authentication was successful!");
    }
}