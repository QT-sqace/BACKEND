package com.example.user_service.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("유효하지 않은 Authorization 헤더입니다.");
    }

    /**
     * JWT 토큰에서 클레임 추출
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT 토큰에서 userId 추출
     */
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.parseLong(claims.getSubject()); // userId 값 추출
    }

    /**
     * Authorization 헤더에서 userId 추출
     */
    public Long extractUserIdFromHeader(String authorizationHeader) {
        String token = extractTokenFromHeader(authorizationHeader);
        return extractUserId(token);
    }
}
