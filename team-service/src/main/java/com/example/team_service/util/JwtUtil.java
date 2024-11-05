package com.example.team_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtUtil {
    //Jwt토큰을 통해서 userId값 추출

    //properties의 secret값 가져오기
    @Value("${secret-key}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        // secretKey를 Key 객체로 변환 (HMAC-SHA 암호화를 위해)
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //JWT 토큰에서 userId값 추출
    public Long extractedUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject()); //userId 값 추출
    }
}
