package com.example.jiralink.repository;

import com.example.jiralink.entity.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
    Optional<UserOauth> findByUserIdAndLinkedProvider(Long userId, String linkedProvider);
    Optional<UserOauth> findByUserId(Long userId);
}