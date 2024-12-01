package com.example.team_service.controller;

import com.example.team_service.dto.response.MeetingDTO;
import com.example.team_service.service.MeetingService;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final JwtUtil jwtUtil;

    // 미팅룸 생성
    @PostMapping("/create")
    public ResponseEntity<?> createMeeting(@RequestParam("teamId") Long teamId,
                                           @RequestParam("meetingName") String meetingName,
                                           @RequestParam(value = "meetingUrl", required = false) String meetingUrl,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);

            MeetingDTO createdMeeting = meetingService.createMeeting(teamId, userId, meetingName, meetingUrl);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "미팅룸 생성 완료");
            response.put("meeting", createdMeeting);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "미팅룸 생성 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 미팅룸 조회 (meetingId로 조회)
    @GetMapping("/{meetingId}")
    public ResponseEntity<?> getMeetingById(@PathVariable Long meetingId) {
        try {
            MeetingDTO meeting = meetingService.getMeetingById(meetingId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "미팅룸 조회 완료");
            response.put("meeting", meeting);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "미팅룸 조회 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 미팅룸 목록 조회 (teamId로 조회-> 전체조회)
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getMeetingsByTeamId(@PathVariable Long teamId) {
        try {
            List<MeetingDTO> meetings = meetingService.getMeetingsByTeamId(teamId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "미팅룸 목록 조회 완료");
            response.put("meetings", meetings);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "미팅룸 목록 조회 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 미팅룸 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMeeting(@RequestParam("meetingId") Long meetingId) {
        try {
            meetingService.deleteMeeting(meetingId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "미팅룸 삭제 완료");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "미팅룸 삭제 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
