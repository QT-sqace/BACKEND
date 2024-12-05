package com.example.user_service.filter;

import com.example.user_service.entity.User;
import com.example.user_service.provider.JwtProvider;
import com.example.user_service.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // /auth 경로는 JWT 검증을 하지 않음
        String path = request.getServletPath();
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //jwt 토큰을 Authorization 헤더에서 추출
            String token = parseBearerToken(request);
            if (token == null)  {
                filterChain.doFilter(request, response);
                return;
            }

            //jwt 토큰 검증 및 클레임 추출
            Map<String, Object> claims = jwtProvider.validate(token);
            if (claims == null) {
                filterChain.doFilter(request, response);
                return;
            }

            //클레임에서 userId 와 provider 정보 추출
            String userId = (String) claims.get("sub");
            //이제는 필요없음 userId로 통일함
//            String provider = (String) claims.get("provider");

            //가입 방식에 따라 다르게 정보 확인
            User user = userRepository.findByUserId(Long.valueOf(userId));

            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            //사용자 권한 확인
            String role = user.getRole();
            log.info("User role: " + role);

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            AbstractAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //보안 컨텍스트에 인증정보 설정
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        //다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    //토큰 값을 추출
    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return null;
        }
        //변경 시작 부분 - 토큰 에러 발생
        String token = authorization.substring(7);
        return StringUtils.hasText(token) ? token : null; // 토큰이 비어 있지 않은지 확인
    }
}
