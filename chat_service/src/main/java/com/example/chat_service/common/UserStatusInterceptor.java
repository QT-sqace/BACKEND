package com.example.chat_service.common;

import com.example.chat_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusInterceptor implements HandlerInterceptor {

    private final UserStatusManager userStatusManager;
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            return true; // 인증 정보가 없는 요청은 무시
        }

        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);
            // 상태 갱신 여부 확인 및 갱신
            if (userStatusManager.canUpdateStatus(userId)) {
                userStatusManager.setOnline(userId);
                log.info("User [{}] status updated to 'online'", userId);
            }
        } catch (Exception e) {
            // 로그로 남기고 처리 진행
            System.out.println("Failed to update user status: " + e.getMessage());
        }

        return true; // 요청 처리 계속 진행
    }
}
