package com.example.jiralink.service;

import com.example.jiralink.entity.UserOauth;
import com.example.jiralink.repository.UserOauthRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JiraProjectService {

    private final UserOauthRepository userOauthRepository;
    private final RestTemplate restTemplate;

    // Jira 프로젝트 목록 조회
    public List<Map<String, Object>> getJiraProjects(Long userId) {
        String accessToken = getJiraAccessToken(userId);
        String cloudId = getCloudIdForUser(userId);

        String url = "https://api.atlassian.com/ex/jira/" + cloudId + "/rest/api/3/project";

        HttpHeaders headers = createHeaders(accessToken);

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Access Token is invalid or expired.", e);
            }
            throw new RuntimeException("Error during Jira API call: " + e.getMessage(), e);
        }
    }

    // Jira 보드 목록 조회
    public List<Map<String, Object>> getBoards(String cloudId, String accessToken) {
        String url = "https://api.atlassian.com/ex/jira/" + cloudId + "/rest/agile/1.0/board";

        HttpHeaders headers = createHeaders(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return (List<Map<String, Object>>) response.getBody().get("values");
    }

    // Jira 스프린트 목록 조회
    public List<Map<String, Object>> getActiveSprints(String cloudId, Long boardId, String accessToken) {
        String url = "https://api.atlassian.com/ex/jira/" + cloudId + "/rest/agile/1.0/board/" + boardId + "/sprint";

        HttpHeaders headers = createHeaders(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return (List<Map<String, Object>>) response.getBody().get("values");
    }

    // Jira 프로젝트의 스프린트 필드 ID 가져오기
    public String getSprintFieldId(String cloudId, String accessToken) {
        String url = "https://api.atlassian.com/ex/jira/" + cloudId + "/rest/api/3/field";

        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody().stream()
                .filter(field -> "Sprint".equals(field.get("name")))
                .map(field -> (String) field.get("id"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sprint 필드가 프로젝트에 없습니다."));
    }

    // Jira 프로젝트 내의 이슈 타입 조회
    public List<Map<String, Object>> getProjectSpecificIssueTypes(String cloudId, String projectId, String accessToken) {
        String url = "https://api.atlassian.com/ex/jira/" + cloudId + "/rest/api/3/project/" + projectId;

        HttpHeaders headers = createHeaders(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> projectData = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<Map<String, Object>>() {}
            );

            if (projectData.containsKey("issueTypes")) {
                return (List<Map<String, Object>>) projectData.get("issueTypes");
            } else {
                throw new RuntimeException("No issue types found for project ID: " + projectId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch issue types: " + e.getMessage(), e);
        }
    }

    // Jira Task 생성
    public void createJiraTask(String cloudId, String projectId, String sprintId, String issueTypeId, String summary, String description, String accessToken) {
        String url = "https://api.atlassian.com/ex/jira/" + cloudId + "/rest/api/3/issue";

        HttpHeaders headers = createHeaders(accessToken);

        // 스프린트 필드 ID 가져오기
        String sprintFieldId = null;
        if (sprintId != null) {
            sprintFieldId = getSprintFieldId(cloudId, accessToken);
        }

        Map<String, Object> fields = new HashMap<>();
        fields.put("summary", summary);
        fields.put("description", Map.of(
                "type", "doc",
                "version", 1,
                "content", List.of(
                        Map.of(
                                "type", "paragraph",
                                "content", List.of(
                                        Map.of(
                                                "text", description,
                                                "type", "text"
                                        )
                                )
                        )
                )
        ));
        fields.put("project", Map.of("id", projectId));
        fields.put("issuetype", Map.of("id", issueTypeId));
        if (sprintId != null && sprintFieldId != null) {
            fields.put(sprintFieldId, Integer.valueOf(sprintId));
        }

        Map<String, Object> body = Map.of("fields", fields);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Status Code: " + e.getStatusCode());
            System.err.println("HTTP Headers: " + e.getResponseHeaders());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create Jira task.", e);
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while creating a Jira task.", e);
        }
    }

    // Access Token 가져오기
    public String getJiraAccessToken(Long userId) {
        return userOauthRepository.findByUserId(userId)
                .map(UserOauth::getAccessToken)
                .orElseThrow(() -> new RuntimeException("Jira Access Token not found for user ID: " + userId));
    }

    // Cloud ID 가져오기
    public String getCloudIdForUser(Long userId) {
        return userOauthRepository.findByUserId(userId)
                .map(UserOauth::getCloudId)
                .orElseThrow(() -> new RuntimeException("Cloud ID not found for user ID: " + userId));
    }

    // HTTP Headers 생성
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}