package com.example.user_service.jira.service;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserOauth;
import com.example.user_service.jira.config.JiraConfig;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserOauthRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
public class JiraAuthServiceImpl implements JiraAuthService {

    private static final Logger logger = LoggerFactory.getLogger(JiraAuthServiceImpl.class);
    private final JiraConfig jiraConfig;
    private final RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOauthRepository userOauthRepository;

    // 액세스 토큰을 임시 저장할 변수
    private String accessTokenCache;

    public JiraAuthServiceImpl(JiraConfig jiraConfig, RestTemplate restTemplate) {
        this.jiraConfig = jiraConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public String requestAccessToken(String code) {
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
                Map responseBody = response.getBody();
                String accessToken = (String) responseBody.get("access_token");

                // 액세스 토큰을 임시로 저장
                this.accessTokenCache = accessToken;

                // 사용자 정보 저장
                saveUserInfo(accessToken);
                logger.info("Access token received and user info saved successfully.");
                return accessToken;
            } else {
                logger.error("Failed to retrieve access token. Status code: {}, Response body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to retrieve access token. Status code: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            logger.error("HttpClientErrorException occurred: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to retrieve access token: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An error occurred while requesting access token: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve access token: " + e.getMessage(), e);
        }
    }

    private void saveUserInfo(String accessToken) {
        String userInfo = getUserInfo(accessToken); // 사용자 정보 가져오기

        // 사용자 정보 파싱 (예시로 이메일과 providerId만 사용)
        Map<String, Object> userInfoMap = parseUserInfo(userInfo);
        String email = (String) userInfoMap.get("email");
        String providerId = (String) userInfoMap.get("account_id");

        // User 엔티티 저장 (이미 존재하는 경우는 생략)
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User(providerId, email, "jira");
            userRepository.save(user);
        }

        // UserOauth 엔티티 저장
        UserOauth userOauth = userOauthRepository.findByUserId(user.getUserId());
        if (userOauth == null) {
            userOauth = new UserOauth();
            userOauth.setUserId(user.getUserId());
            userOauth.setLinkedProvider("jira");
            userOauth.setLinkedProviderId(providerId);
        }
        userOauth.setAccessToken(accessToken);
        userOauth.setLinkedAt(new Timestamp(System.currentTimeMillis()));
        userOauthRepository.save(userOauth);
    }

    private Map<String, Object> parseUserInfo(String userInfo) {
        Map<String, Object> userInfoMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 파싱
            JsonNode root = objectMapper.readTree(userInfo);
            String email = root.path("email").asText();
            String accountId = root.path("account_id").asText();

            // 필요한 정보를 맵에 추가
            userInfoMap.put("email", email);
            userInfoMap.put("account_id", accountId);

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse user info JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to parse user info JSON", e);
        }

        return userInfoMap;
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
                logger.error("Failed to retrieve user info. Status code: {}, Response body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to retrieve user info. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            logger.error("HttpClientErrorException occurred: Status code: {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to retrieve user info: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An error occurred while retrieving user info: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve user info: " + e.getMessage(), e);
        }
    }

    @Override
    public String getStoredAccessToken() {
        if (accessTokenCache == null) {
            throw new RuntimeException("No access token found. Please authenticate first.");
        }
        return accessTokenCache;
    }
}