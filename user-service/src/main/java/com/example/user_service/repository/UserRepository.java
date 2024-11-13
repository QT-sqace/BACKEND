package com.example.user_service.repository;

import com.example.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long userId);
    User findByEmail(String email);

    //이메일로 db에 이미 존재하는 회원인지 검사후 db 저장에 사용
    boolean existsByEmail(String email);

    //토큰 검증에 사용 -> jwt userId로 통일하면서 사용x
    User findByProviderId(String id);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
