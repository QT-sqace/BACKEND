package com.example.team_service.controller;

import com.example.team_service.dto.request.NoticeCreateRequestDto;
import com.example.team_service.dto.request.NoticeUpdateRequestDto;
import com.example.team_service.dto.response.NoticeDTO;
import com.example.team_service.service.NoticeService;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtUtil jwtUtil;

    // 공지사항 생성
    @PostMapping
    public ResponseEntity<?> createNotice(
            @RequestHeader("Authorization") String token,
            @RequestBody NoticeCreateRequestDto request
    ) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);

            // 팀 멤버 여부 확인
            if (!noticeService.isUserTeamMember(userId, request.getTeamMemberId())) {
                return createErrorResponse("권한이 없습니다: 요청한 사용자는 해당 팀의 멤버가 아닙니다.");
            }

            NoticeDTO noticeDTO = noticeService.createNotice(
                    request.getTitle(),
                    request.getContent(),
                    request.getTeamMemberId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "공지사항 생성 완료");
            response.put("data", noticeDTO);

            logResponse(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse("공지사항 생성 실패: " + e.getMessage());
        }
    }

    // 공지사항 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoticeById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        try {
            jwtUtil.extractedUserIdFromHeader(token); // 사용자 검증

            NoticeDTO noticeDTO = noticeService.getNoticeById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "공지사항 조회 완료");
            response.put("data", noticeDTO);

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("공지사항 조회 실패: " + e.getMessage());
        }
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotice(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody NoticeUpdateRequestDto request
    ) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);

            // 작성자 여부 확인
            if (!noticeService.isNoticeOwner(userId, id)) {
                return createErrorResponse("권한이 없습니다: 요청한 사용자는 해당 공지사항의 작성자가 아닙니다.");
            }

            NoticeDTO noticeDTO = noticeService.updateNotice(
                    id,
                    request.getTitle(),
                    request.getContent()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "공지사항 수정 완료");
            response.put("data", noticeDTO);

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("공지사항 수정 실패: " + e.getMessage());
        }
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);

            // 작성자 여부 확인
            if (!noticeService.isNoticeOwner(userId, id)) {
                return createErrorResponse("권한이 없습니다: 요청한 사용자는 해당 공지사항의 작성자가 아닙니다.");
            }

            noticeService.deleteNotice(id);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "공지사항 삭제 완료");

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("공지사항 삭제 실패: " + e.getMessage());
        }
    }

    // 공지사항 목록 조회
    @GetMapping
    public ResponseEntity<?> getAllNotices(@RequestHeader("Authorization") String token) {
        try {
            jwtUtil.extractedUserIdFromHeader(token); // 사용자 검증

            List<NoticeDTO> notices = noticeService.getAllNotices();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "공지사항 목록 조회 완료");
            response.put("data", notices);

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("공지사항 목록 조회 실패: " + e.getMessage());
        }
    }

    // 작성자별 공지사항 조회
    @GetMapping("/by-author/{teamMemberId}")
    public ResponseEntity<?> getNoticesByAuthor(
            @RequestHeader("Authorization") String token,
            @PathVariable Long teamMemberId
    ) {
        try {
            jwtUtil.extractedUserIdFromHeader(token); // 사용자 검증

            List<NoticeDTO> notices = noticeService.getNoticesByAuthor(teamMemberId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "작성자별 공지사항 조회 완료");
            response.put("data", notices);

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse("작성자별 공지사항 조회 실패: " + e.getMessage());
        }
    }

    // 에러 응답 생성
    private ResponseEntity<Map<String, Object>> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "fail");
        response.put("message", errorMessage);

        logResponse(response);
        return ResponseEntity.badRequest().body(response);
    }

    // 응답 로그 출력
    private void logResponse(Map<String, Object> response) {
        System.out.println("응답 로그: " + response);
    }
}
