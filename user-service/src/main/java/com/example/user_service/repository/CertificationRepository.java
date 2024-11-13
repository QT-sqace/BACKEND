package com.example.user_service.repository;

import com.example.user_service.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Certification findByEmail(String email);

    //하나의 이메일에 대해서 항상 하나의 인증코드 유지
    @Transactional
    void deleteByEmail(String email);
}
