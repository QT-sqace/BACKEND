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

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final JwtUtil jwtUtil;

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file,
                                           @RequestParam("teamId") Long teamId,
                                           @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.extractedUserIdFromHeader(token); // 토큰에서 사용자 ID 추출
            File uploadedFile = fileService.uploadFile(file, teamId, userId);
            return ResponseEntity.ok(uploadedFile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 파일 삭제
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.ok("파일 삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 삭제 실패: " + e.getMessage());
        }
    }

    // 파일 다운로드
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // 파일 리소스 가져오기
            Resource resource = fileService.downloadFile(fileId);

            // 파일 이름 및 확장자 추출
            String fileName = resource.getFilename();
            if (fileName == null) {
                return ResponseEntity.badRequest().body(null);
            }
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

            // 확장자에 따른 Content-Type 설정
            String contentType;
            switch (fileExtension) {
                case "jpg":
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                case "pdf":
                    contentType = "application/pdf";
                    break;
                default:
                    contentType = "application/octet-stream"; // 기본값
            }

            // 응답 헤더 설정
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
