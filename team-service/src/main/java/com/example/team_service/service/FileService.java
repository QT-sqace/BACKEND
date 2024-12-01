package com.example.team_service.service;

import com.example.team_service.dto.response.FileDTO;
import com.example.team_service.entity.File;
import com.example.team_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    // 파일 업로드 (단일/다중 지원)
    public List<FileDTO> uploadFiles(List<MultipartFile> files, Long teamId, Long userId) throws Exception {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 업로드 폴더 생성
        }

        List<FileDTO> uploadedFileDTOs = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String filePath = uploadDir + file.getOriginalFilename();

                // 파일 저장
                Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

                // 파일 엔티티 생성 및 데이터베이스 저장
                File fileEntity = new File();
                fileEntity.setFileName(file.getOriginalFilename());
                fileEntity.setFilePath(filePath);
                fileEntity.setFileSize((int) file.getSize());
                fileEntity.setUploadedBy(userId);
                fileEntity.setTeamId(teamId);
                fileEntity.setUploadDate(LocalDateTime.now());

                File savedFile = fileRepository.save(fileEntity); // DB 저장

                // FileDTO로 변환하여 리스트에 추가
                uploadedFileDTOs.add(convertToFileDTO(savedFile));

                System.out.println("파일 업로드 성공: " + filePath);
            } catch (Exception e) {
                System.err.println("파일 업로드 중 오류 발생: " + e.getMessage());
            }
        }

        if (uploadedFileDTOs.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

        return uploadedFileDTOs;
    }

    // 파일 삭제 (단일/다중 지원)
    public void deleteFiles(List<Long> fileIds) throws Exception {
        for (Long fileId : fileIds) {
            try {
                File fileEntity = fileRepository.findById(fileId)
                        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileId));

                // 실제 파일 삭제
                Path filePath = Paths.get(fileEntity.getFilePath());
                Files.deleteIfExists(filePath);

                // 데이터베이스에서 엔티티 삭제
                fileRepository.delete(fileEntity);

                System.out.println("파일 삭제 성공: " + filePath);
            } catch (Exception e) {
                System.err.println("파일 삭제 중 오류 발생 (fileId: " + fileId + "): " + e.getMessage());
            }
        }
    }

    // 파일 다운로드 (단일/다중 지원)
    public List<Resource> downloadFiles(List<Long> fileIds) throws Exception {
        List<Resource> resources = new ArrayList<>();

        for (Long fileId : fileIds) {
            try {
                // 파일 정보 조회
                File fileEntity = fileRepository.findById(fileId)
                        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + fileId));

                // 파일 경로 생성
                Path filePath = Paths.get(fileEntity.getFilePath());
                System.out.println("확인 중인 파일 경로: " + filePath);

                // 파일 존재 여부 확인
                if (!Files.exists(filePath)) {
                    throw new IllegalArgumentException("파일 경로가 유효하지 않습니다: " + filePath);
                }

                // Resource 객체 생성 및 추가
                UrlResource resource = new UrlResource(filePath.toUri());
                if (!resource.exists() || !resource.isReadable()) {
                    throw new IllegalArgumentException("파일이 읽기 불가능합니다: " + filePath);
                }

                resources.add(resource);
                System.out.println("파일 다운로드 성공: " + filePath);
            } catch (Exception e) {
                System.err.println("파일 다운로드 중 오류 발생 (fileId: " + fileId + "): " + e.getMessage());
            }
        }

        if (resources.isEmpty()) {
            throw new IllegalArgumentException("유효한 파일이 없습니다.");
        }

        return resources;
    }

    // 다중 파일 ZIP 생성
    public Resource createZipFile(List<Resource> resources) throws Exception {
        String zipFileName = "uploads/files.zip";
        Path zipPath = Paths.get(zipFileName);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    ZipEntry entry = new ZipEntry(resource.getFilename());
                    zos.putNextEntry(entry);
                    is.transferTo(zos);
                    zos.closeEntry();
                }
            }
        } catch (Exception e) {
            System.err.println("ZIP 파일 생성 중 오류 발생: " + e.getMessage());
            throw e;
        }

        System.out.println("ZIP 파일 생성 성공: " + zipPath);
        return new UrlResource(zipPath.toUri());
    }

    // 특정 팀 ID로 파일 조회
    public List<FileDTO> getFilesByTeamId(Long teamId) {
        List<File> files = fileRepository.findAllByTeamId(teamId);

        return files.stream()
                .map(this::convertToFileDTO)
                .collect(Collectors.toList());
    }

    // File 엔티티를 FileDTO로 변환
    private FileDTO convertToFileDTO(File file) {
        return FileDTO.builder()
                .fileId(file.getFileId())
                .teamId(file.getTeamId())
                .fileName(file.getFileName())
                .filePath(file.getFilePath())
                .fileSize(file.getFileSize())
                .uploadedBy(file.getUploadedBy())
                .uploadDate(file.getUploadDate())
                .build();
    }
}
