package com.example.jiralink.service;

import com.example.jiralink.entity.TeamJiraMapping;
import com.example.jiralink.repository.TeamJiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeamJiraService {

    private final TeamJiraRepository teamJiraRepository;

    // 팀과 Jira 프로젝트 연결
    public void linkJiraProject(Long userId, Long teamId, String jiraProjectId, String jiraProjectName) {
        // 기존 매핑이 존재하는지 확인
        boolean exists = teamJiraRepository.existsByUserIdAndTeamIdAndJiraProjectId(userId, teamId, jiraProjectId);

        if (exists) {
            throw new IllegalArgumentException("The Jira project is already linked to this team by the same user.");
        }

        // 매핑이 존재하지 않으면 저장
        TeamJiraMapping mapping = new TeamJiraMapping();
        mapping.setUserId(userId);
        mapping.setTeamId(teamId);
        mapping.setJiraProjectId(jiraProjectId);
        mapping.setJiraProjectName(jiraProjectName);

        teamJiraRepository.save(mapping);
    }

    // 팀에 연결된 Jira 프로젝트 정보 조회
    public Map<String, String> getLinkedJiraProject(Long teamId) {
        TeamJiraMapping mapping = findMappingByTeamId(teamId);

        return Map.of(
                "jiraProjectId", mapping.getJiraProjectId(),
                "jiraProjectName", mapping.getJiraProjectName()
        );
    }

    // 팀과 Jira 프로젝트 연결 해제
    public void unlinkJiraProject(Long teamId) {
        teamJiraRepository.deleteByTeamId(teamId);
    }

    // 공통적으로 사용하는 팀 ID 기반 매핑 조회 메서드
    private TeamJiraMapping findMappingByTeamId(Long teamId) {
        return teamJiraRepository.findFirstByTeamId(teamId)
                .orElseThrow(() -> new JiraProjectNotFoundException("No Jira project linked to this team."));
    }

    // 사용자 정의 예외 클래스
    public static class JiraProjectNotFoundException extends RuntimeException {
        public JiraProjectNotFoundException(String message) {
            super(message);
        }
    }
}