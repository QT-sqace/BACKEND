package sqace.teamchat_demo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sqace.teamchat_demo.service.FileStorageService;

import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
public class ChatController {

    private final FileStorageService fileStorageService;

    @Autowired
    public ChatController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // 업로드된 파일 목록을 저장할 리스트 (테스트용)
    private List<String> uploadedFiles = new ArrayList<>();

    @GetMapping("/")
    public String home(Model model) {
        log.info("Accessed home page.");
        model.addAttribute("uploadedFiles", uploadedFiles);  // 업로드된 파일 목록을 모델에 추가
        return "chater";  // 기본 페이지를 `chater.html`로 설정
    }

    @GetMapping("/chat")
    public String chatGET(Model model) {
        log.info("@ChatController, chat GET()");
        model.addAttribute("uploadedFiles", uploadedFiles);  // 업로드된 파일 목록을 모델에 추가
        return "chater";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            String fileName = fileStorageService.saveFile(file); // 파일 저장
            log.info("Uploaded file: {}", fileName);
            uploadedFiles.add(fileName);  // 업로드된 파일 목록에 추가
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + fileName);
        } catch (Exception e) {
            log.error("File upload failed", e);
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        }
        return "redirect:/chat";
    }
}
