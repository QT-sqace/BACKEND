package com.example.user_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "user_oauth")
public class UserOauth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oauthMappingId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String linkedProvider;

    @Column(nullable = false)
    private String linkedProviderId;

    private String accessToken;
    private String refreshToken;
    private Long expiresAt;
    private Timestamp linkedAt;
}
