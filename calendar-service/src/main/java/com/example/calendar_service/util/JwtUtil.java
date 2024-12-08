package com.example.calendar_service.util;

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

    //userId값을 추출하는 메서드
    public Long extractedUserIdFromHeader(String authorizationHeader) {
        String token = parseBearerToken(authorizationHeader);
        return extractedUserId(token);
    }

    //Authorization 헤더에서 Bearer 토큰 추출
    private String parseBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("유효하지 않은 Authorization 헤더입니다.");
    }
}
