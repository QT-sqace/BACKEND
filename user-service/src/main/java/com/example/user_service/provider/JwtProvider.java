package com.example.user_service.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider {

    @Value("${secret-key}")
    private String secretKey;

    // JWT 생성 시 ID와 provider를 포함하여 생성
    public String create(String id, String provider) {

        Date expiredDate = Date.from(Instant.now().plus(24, ChronoUnit.HOURS));
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(id)
                .claim("provider", provider)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();
    }

/*    // JWT 검증 및 클레임 추출
//아래 예외처리가 더 정확해서 아래꺼 사용
    public Map<String, Object> validate(String jwt) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }*/

    // JwtProvider.java - validate 메서드 수정
    //토큰 생성 에러 확인용
    public Map<String, Object> validate(String jwt) {
        if (!StringUtils.hasText(jwt)) {

            return null; // 토큰이 비어 있으면 null 반환
        }

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Malformed JWT: " + e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error parsing JWT: " + e.getMessage());
            return null;
        }
    }
}

