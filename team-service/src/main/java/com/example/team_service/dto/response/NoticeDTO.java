package com.example.team_service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NoticeDTO {
    private Long noticeId;
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 날짜와 시간 모두 표시
    private LocalDateTime createdDate;

    private Long userId; // 작성자 ID
    private String role; // 작성자 역할 (MASTER, ADMIN, MEMBER)
}
