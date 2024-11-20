package com.example.team_service.service;

import com.example.team_service.entity.File;
import com.example.team_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    // 파일 업로드
    public File uploadFile(MultipartFile file, Long teamId, Long userId) throws Exception {
        // 저장 경로 지정
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 업로드 폴더가 없으면 생성 -> 추후에 배포 전 minio 경로로 수정 필요
        }

        String filePath = uploadDir + file.getOriginalFilename();

        // 파일 저장
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // 파일 엔티티 생성 및 저장
        File fileEntity = new File();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFilePath(filePath);
        fileEntity.setFileSize((int) file.getSize());
        fileEntity.setUploadedBy(userId);
        fileEntity.setTeamId(teamId);
        fileEntity.setUploadDate(LocalDateTime.now());
        return fileRepository.save(fileEntity);
    }

    // 파일 삭제
    public void deleteFile(Long fileId) throws Exception {
        // 데이터베이스에서 파일 정보 가져오기
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 실제 파일 삭제
        Path filePath = Paths.get(fileEntity.getFilePath());
        Files.deleteIfExists(filePath);

        // 데이터베이스에서 엔티티 삭제
        fileRepository.delete(fileEntity);
    }

    // 파일 다운로드
    public Resource downloadFile(Long fileId) throws Exception {
        // 데이터베이스에서 파일 정보 가져오기
        File fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 파일 경로 확인
        Path filePath = Paths.get(fileEntity.getFilePath());
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("파일 경로가 유효하지 않습니다.");
        }

        // 리소스 반환
        return new UrlResource(filePath.toUri());
    }
}
