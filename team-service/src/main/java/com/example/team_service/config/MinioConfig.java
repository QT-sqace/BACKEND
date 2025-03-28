package com.example.team_service.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.server.url}")
    private String url;
    @Value("${minio.server.accessKey}")
    private String accessKey;
    @Value("${minio.server.userid}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url) // Minio 서버 주소
                .credentials(accessKey, secretKey) // 인증 키
                .build();
    }
}
