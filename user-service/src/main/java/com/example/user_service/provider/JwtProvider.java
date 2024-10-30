package com.example.user_service.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

//@Component
/*
public class JwtProvider {

    @Value("${secret-key}")
    private String secretKey;

    //토큰 설정 검사
    public String create(String id, String provider) {

        //만료시간 지정 24 넣으면 24시간이후 만료
        Date expiredDate = Date.from(Instant.now().plus(24, ChronoUnit.HOURS));
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String jwt = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(id)
                .claim("provider", provider)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .compact();

        return jwt;
    }

    //jwt 검증 로직 -> 추후에는 gateway로 옮겨야함
    public String validate(String jwt) {

        String subject = null;
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return subject;
    }
}
*/
@Component
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

    // JWT 검증 및 클레임 추출
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
    }
}

