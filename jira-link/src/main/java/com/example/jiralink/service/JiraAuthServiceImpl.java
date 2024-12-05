package com.example.jiralink.service;

import com.example.jiralink.entity.UserOauth;
import com.example.jiralink.config.JiraConfig;
import com.example.jiralink.repository.UserOauthRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JiraAuthServiceImpl implements JiraAuthService {

    private static final Logger logger = LoggerFactory.getLogger(JiraAuthServiceImpl.class);
    private final JiraConfig jiraConfig;
    private final RestTemplate restTemplate;
    private final UserOauthRepository userOauthRepository;

    @Override
    public String requestAccessToken(Long userId, String code) {
        String tokenUrl = "https://auth.atlassian.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("client_id", jiraConfig.getClientId());
        body.put("client_secret", jiraConfig.getClientSecret());
        body.put("code", code);
        body.put("redirect_uri", jiraConfig.getRedirectUri());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    String accessToken = (String) responseBody.get("access_token");

                    // CloudId 조회
                    String cloudId = fetchCloudId(accessToken);

                    // AccessToken 및 CloudId 저장
                    updateAccessTokenAndCloudId(userId, accessToken, cloudId);

                    return accessToken;
                }
            }
            throw new RuntimeException("Failed to retrieve access token");
        } catch (Exception e) {
            logger.error("Error during access token request: {}", e.getMessage());
            throw new RuntimeException("Error during access token request: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateAccessTokenAndCloudId(Long userId, String accessToken, String cloudId) {
        UserOauth userOauth = userOauthRepository.findByUserId(userId)
                .orElse(new UserOauth()); // 기존 데이터가 없으면 새로 생성

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0); // 초와 밀리초 제거
        Timestamp timestamp = Timestamp.valueOf(now);

        userOauth.setUserId(userId);
        userOauth.setAccessToken(accessToken);
        userOauth.setCloudId(cloudId);
        userOauth.setLinkedProvider("jira");
        userOauth.setLinkedAt(timestamp);

        userOauthRepository.save(userOauth);
        logger.info("AccessToken and CloudId updated for user ID: {}", userId);
    }

    public String fetchCloudId(String accessToken) {
        String url = "https://api.atlassian.com/oauth/token/accessible-resources";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    (Class<List<Map<String, Object>>>) (Object) List.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> resources = response.getBody();
                if (resources != null && !resources.isEmpty()) {
                    return (String) resources.get(0).get("id"); // 첫 번째 CloudId 반환
                }
            }
            throw new RuntimeException("No accessible resources found");
        } catch (Exception e) {
            logger.error("Error fetching cloudId: {}", e.getMessage());
            throw new RuntimeException("Error fetching cloudId: " + e.getMessage(), e);
        }
    }

    @Override
    public String getUserInfo(String accessToken) {
        String userInfoUrl = "https://api.atlassian.com/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("User info retrieved successfully.");
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to retrieve user info. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error fetching user info: {}", e.getMessage());
            throw new RuntimeException("Error fetching user info: " + e.getMessage(), e);
        }
    }
}