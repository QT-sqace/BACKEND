package com.example.team_service.controller;

import com.example.team_service.entity.File;
import com.example.team_service.service.FileService;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final JwtUtil jwtUtil;

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestParam("files") List<MultipartFile> files,
                                         @RequestParam("teamId") Long teamId,
                                         @RequestHeader("Authorization") String token)

    {  System.out.println("uploadFiles API called");

        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);
            List<File> uploadedFiles = fileService.uploadFiles(files, teamId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "파일 업로드 완료");
            response.put("files", uploadedFiles);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 업로드 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 파일 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFiles(@RequestBody List<Long> fileIds) {
        try {
            fileService.deleteFiles(fileIds);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "파일 삭제 완료");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 삭제 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 파일 다운로드
    @PostMapping("/download")
    public ResponseEntity<?> downloadFiles(@RequestBody List<Long> fileIds) {
        try {
            List<Resource> resources = fileService.downloadFiles(fileIds);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "파일 다운로드 성공");
            response.put("resources", resources);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 다운로드 실패: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // 파일 조회 API - 팀 ID로 파일 목록 조회
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> listFiles(@PathVariable Long teamId) {
        try {
            List<File> files = fileService.getFilesByTeamId(teamId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("files", files);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}
