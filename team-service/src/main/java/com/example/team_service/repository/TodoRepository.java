package com.example.team_service.repository;

import com.example.team_service.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByTeam_TeamIdAndUserId(Long teamId, Long userId);
    List<Todo> findAllByTeam_TeamId(Long teamId);
}
