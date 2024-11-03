package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "certification")
@Table(name = "certification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Certification {

    //이메일 검증용 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificationId;
    private String email;
    private String certificationNumber;
}
