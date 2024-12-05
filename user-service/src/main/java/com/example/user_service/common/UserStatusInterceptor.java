package com.example.user_service.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
public class UserStatusInterceptor implements HandlerInterceptor {

    private final UserStatusManager userStatusManager;
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없으면 상태 갱신하지 않음
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return true; // 요청 처리 계속 진행
        }

        try {
            // JWT 토큰에서 userId 추출
            Long userId = jwtUtil.extractUserIdFromHeader(authorizationHeader);

            // 상태 갱신 여부 확인 및 갱신
            if (userStatusManager.canUpdateStatus(userId)) {
                userStatusManager.setOnline(userId);
                log.info("User [{}] status updated to 'online'", userId);
            }
        } catch (Exception e) {
            log.warn("Failed to update user status: {}", e.getMessage());
        }

        return true; // 요청 처리 계속 진행
    }
}
