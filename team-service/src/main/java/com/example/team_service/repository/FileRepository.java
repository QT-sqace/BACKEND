package com.example.team_service.repository;

import com.example.team_service.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository

public interface FileRepository extends JpaRepository<File, Long> {
    // 특정 팀 ID로 파일 조회
    List<File> findAllByTeamId(Long teamId);
}