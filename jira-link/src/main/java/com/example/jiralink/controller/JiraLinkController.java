package com.example.jiralink.controller;

import com.example.jiralink.dto.JiraLinkRequest;
import com.example.jiralink.service.JiraProjectService;
import com.example.jiralink.service.TeamIntegrationService;
import com.example.jiralink.service.TeamJiraService;
import com.example.jiralink.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jira")
@RequiredArgsConstructor
public class JiraLinkController {

    private final TeamIntegrationService teamIntegrationService;
    private final TeamJiraService teamJiraService;
    private final JiraProjectService jiraProjectService;
    private final JwtUtil jwtUtil;

    // Jira 프로젝트 목록 조회
    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> getProjects(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractedUserIdFromHeader(token);

        // Jira 프로젝트 목록 가져오기
        List<Map<String, Object>> projects = jiraProjectService.getJiraProjects(userId);
        return ResponseEntity.ok(projects);
    }

    // 팀에 Jira 프로젝트 연동
    @PostMapping("/teams/{teamId}/link")
    public ResponseEntity<String> linkJiraToTeam(
            @RequestHeader("Authorization") String token,
            @PathVariable Long teamId,
            @RequestBody JiraLinkRequest request) {

        Long userId = jwtUtil.extractedUserIdFromHeader(token);

        // 팀 존재 여부 확인 및 Jira 프로젝트 연동
        teamIntegrationService.getTeam(teamId); // 팀 존재 여부 확인
        teamJiraService.linkJiraProject(userId, teamId, request.getJiraProjectId(), request.getJiraProjectName());
        return ResponseEntity.ok("Jira project linked to team successfully.");
    }

    // 연동된 Jira 프로젝트 정보 조회
    @GetMapping("/teams/{teamId}/linked")
    public ResponseEntity<Map<String, String>> getLinkedJiraProject(@PathVariable Long teamId) {
        Map<String, String> jiraProjectInfo = teamJiraService.getLinkedJiraProject(teamId);
        return ResponseEntity.ok(jiraProjectInfo);
    }

    // 팀에서 Jira 프로젝트 연동 해제
    @DeleteMapping("/teams/{teamId}/unlink")
    public ResponseEntity<String> unlinkJiraFromTeam(@PathVariable Long teamId) {
        teamJiraService.unlinkJiraProject(teamId);
        return ResponseEntity.ok("Jira project unlinked from team successfully.");
    }

    @GetMapping("/projects/{projectId}/issuetypes")
    public ResponseEntity<List<Map<String, String>>> getIssueTypesForProject(
            @RequestHeader("Authorization") String token,
            @PathVariable String projectId) {

        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        String cloudId = jiraProjectService.getCloudIdForUser(userId);
        String accessToken = jiraProjectService.getJiraAccessToken(userId);

        // 특정 프로젝트의 이슈 타입 조회
        List<Map<String, Object>> issueTypes = jiraProjectService.getProjectSpecificIssueTypes(cloudId, projectId, accessToken);

        // 필요한 데이터만 추출하여 반환
        return ResponseEntity.ok(
                issueTypes.stream()
                        .map(issueType -> Map.of(
                                "id", (String) issueType.get("id"),
                                "name", (String) issueType.get("name"),
                                "description", (String) issueType.get("description")
                        ))
                        .toList()
        );
    }

    //Jira 보드 목록 조회
    @GetMapping("/boards")
    public ResponseEntity<List<Map<String, Object>>> getJiraBoards(
            @RequestHeader("Authorization") String token) {

        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        String cloudId = jiraProjectService.getCloudIdForUser(userId);
        String jiraAccessToken = jiraProjectService.getJiraAccessToken(userId);

        List<Map<String, Object>> boards = jiraProjectService.getBoards(cloudId, jiraAccessToken);

        return ResponseEntity.ok(boards);
    }

    //Jira 스프린트 목록 조회
    @GetMapping("/boards/{boardId}/sprints")
    public ResponseEntity<List<Map<String, Object>>> getActiveSprints(
            @RequestHeader("Authorization") String token,
            @PathVariable Long boardId) {

        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        String cloudId = jiraProjectService.getCloudIdForUser(userId);
        String jiraAccessToken = jiraProjectService.getJiraAccessToken(userId);

        // Jira 스프린트 목록 조회
        List<Map<String, Object>> allSprints = jiraProjectService.getActiveSprints(cloudId, boardId, jiraAccessToken);

        // state가 "active"인 스프린트만 필터링
        List<Map<String, Object>> activeSprints = allSprints.stream()
                .filter(sprint -> "active".equals(sprint.get("state")))
                .toList();

        return ResponseEntity.ok(activeSprints);
    }

    // Jira에 업무 생성
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<String> addTaskToJira(
            @RequestHeader("Authorization") String token,
            @PathVariable String projectId,
            @RequestBody Map<String, Object> request) {

        Long userId = jwtUtil.extractedUserIdFromHeader(token);
        String accessToken = jiraProjectService.getJiraAccessToken(userId);
        String cloudId = jiraProjectService.getCloudIdForUser(userId);

        // 요청에서 데이터 추출
        String summary = (String) request.get("summary");
        String description = (String) request.get("description");
        String issueTypeId = (String) request.get("issueTypeId");
        Integer sprintId = (Integer) request.get("sprintId"); // 스프린트는 선택 사항

        // 태스크 생성
        jiraProjectService.createJiraTask(cloudId, projectId, String.valueOf(sprintId), issueTypeId, summary, description, accessToken);

        return ResponseEntity.ok("Task created successfully!");
    }
}