package com.example.team_service.controller;

import com.example.team_service.dto.response.FileDTO;
import com.example.team_service.service.FileService;
import com.example.team_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
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
                                         @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token);

            // FileDTO 리스트 반환
            List<FileDTO> uploadedFiles = fileService.uploadFiles(files, teamId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "파일 업로드 완료");
            response.put("files", uploadedFiles);

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 업로드 실패: " + e.getMessage());

            logResponse(response);
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

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 삭제 실패: " + e.getMessage());

            logResponse(response);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 파일 조회 API - 팀 ID로 파일 목록 조회
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> listFiles(@PathVariable Long teamId) {
        try {
            // FileDTO 리스트 반환
            List<FileDTO> files = fileService.getFilesByTeamId(teamId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "파일 조회 완료");
            response.put("files", files);

            logResponse(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 조회 실패: " + e.getMessage());

            logResponse(response);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 파일 다운로드 (단일/다중 지원)
    @PostMapping("/download")
    public ResponseEntity<?> downloadFiles(@RequestBody List<Long> fileIds) {
        try {
            List<Resource> resources = fileService.downloadFiles(fileIds);

            if (resources.isEmpty()) {
                throw new IllegalArgumentException("유효한 파일이 없습니다.");
            }

            if (resources.size() == 1) {
                // 단일 파일 처리
                Resource singleFile = resources.get(0);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + singleFile.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(singleFile.getURI())))
                        .body(singleFile);
            } else {
                // 다중 파일 처리: ZIP으로 압축
                Resource zipFile = fileService.createZipFile(resources);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"files.zip\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/zip")
                        .body(zipFile);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "파일 다운로드 실패: " + e.getMessage());

            logResponse(response);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 로그 출력 메서드 (JSON 형식)
    private void logResponse(Map<String, Object> response) {
        System.out.println("응답 로그: " + response);
    }
}
