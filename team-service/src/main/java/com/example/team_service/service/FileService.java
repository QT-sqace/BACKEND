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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    // 파일 업로드 (단일/다중 지원)
    public List<File> uploadFiles(List<MultipartFile> files, Long teamId, Long userId) throws Exception {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 업로드 폴더가 없으면 생성
        }

        List<File> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String filePath = uploadDir + file.getOriginalFilename();

            // 파일 저장
            Files.copy(file.getInputStream(), Paths.get(filePath));

            // 파일 엔티티 생성 및 데이터베이스 저장
            File fileEntity = new File();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFilePath(filePath);
            fileEntity.setFileSize((int) file.getSize());
            fileEntity.setUploadedBy(userId);
            fileEntity.setTeamId(teamId);
            fileEntity.setUploadDate(LocalDateTime.now());
            uploadedFiles.add(fileRepository.save(fileEntity)); // DB 저장
        }

        return uploadedFiles;
    }

    // 파일 삭제 (단일/다중 지원)
    public void deleteFiles(List<Long> fileIds) throws Exception {
        for (Long fileId : fileIds) {
            File fileEntity = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileId));

            // 실제 파일 삭제
            Path filePath = Paths.get(fileEntity.getFilePath());
            Files.deleteIfExists(filePath);

            // 데이터베이스에서 엔티티 삭제
            fileRepository.delete(fileEntity);
        }
    }

    // 파일 다운로드 (단일/다중 지원)
    public List<Resource> downloadFiles(List<Long> fileIds) throws Exception {
        List<Resource> resources = new ArrayList<>();

        for (Long fileId : fileIds) {
            File fileEntity = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileId));

            Path filePath = Paths.get(fileEntity.getFilePath());
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("파일 경로가 유효하지 않습니다: " + fileId);
            }

            resources.add(new UrlResource(filePath.toUri()));
        }

        return resources;
    }

    // 특정 팀 ID로 파일 조회
    public List<File> getFilesByTeamId(Long teamId) {
        return fileRepository.findAllByTeamId(teamId);
    }
}
