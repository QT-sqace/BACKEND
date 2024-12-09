package com.example.jiralink.repository;

import com.example.jiralink.entity.TeamJiraMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamJiraRepository extends JpaRepository<TeamJiraMapping, Long> {
    boolean existsByUserIdAndTeamIdAndJiraProjectId(Long userId, Long teamId, String jiraProjectId);
    Optional<TeamJiraMapping> findFirstByTeamId(Long teamId);
    void deleteByTeamId(Long teamId);
}