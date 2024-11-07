package com.example.user_service.repository;

import com.example.user_service.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUserId(Long userId);
}
