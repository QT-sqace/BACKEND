package com.example.user_service.repository;

import com.example.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long userId);
    User findByEmail(String email);

    //이메일로 db에 이미 존재하는 회원인지 검사후 db 저장에 사용
    boolean existsByEmail(String email);

    //토큰 검증에 사용
    User findByProviderId(String id);
}
