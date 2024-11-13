package com.example.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${secret-key}")
    private String secretKey;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("JwtAuthenticationFilter is processing the request for URI: {}", exchange.getRequest().getURI());
            String token = getBearerToken(exchange);
            if (token == null) {
                log.warn("JWT Token not found in request headers");
                log.warn("jwt 토큰이 헤더에서 발견이 안되었음 !!!!!!!!!!!!!!!!!!!!");
                return this.onError(exchange, "Missing JWT token", HttpStatus.UNAUTHORIZED);
            }
            if (!isTokenValid(token)) {
                log.warn("Invalid JWT token detected");
                log.warn("유효하지 않은 토큰임!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                return this.onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }
            log.info("JWT token is valid for the request");
            log.info("정상적으로 jwt 검증 성공!!!!!!!!!!!!!!!!!!!!!!!!!");
            return chain.filter(exchange);
        };
    }

    // Authorization 헤더에서 JWT 추출
    private String getBearerToken(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        return (authorization != null && authorization.startsWith("Bearer ")) ? authorization.substring(7) : null;
    }

    // JWT 토큰 유효성 검사
    private boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims != null;
        } catch (Exception e) {
            return false;
        }
    }

    // 에러 발생 시 응답
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // 추가 설정이 필요하면 여기에 정의
    }
}
