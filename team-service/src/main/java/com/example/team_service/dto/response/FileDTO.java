package com.example.team_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;


@Getter
@Builder
public class FileDTO {
    private Long fileId;
    private Long teamId;
    private String fileName;
    private String filePath;
    private int fileSize;
    private Long uploadedBy;
    private String userName; // 파일 업로드 사용자 이름

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 날짜 포맷 지정
    private LocalDateTime uploadDate;
}
