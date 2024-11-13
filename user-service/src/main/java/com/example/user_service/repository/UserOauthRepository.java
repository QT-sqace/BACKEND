package com.example.user_service.repository;

import com.example.user_service.entity.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
    UserOauth findByUserId(Long userId);
}