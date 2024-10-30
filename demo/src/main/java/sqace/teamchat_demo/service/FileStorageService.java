package sqace.teamchat_demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final Path storageLocation = Paths.get("uploads");

    public FileStorageService() throws IOException {
        Files.createDirectories(storageLocation); // 저장할 폴더가 없으면 생성
    }

    public String saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            Path targetLocation = storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation); // 파일 저장
            return fileName;
        }
        throw new IOException("Invalid file name");
    }
}
