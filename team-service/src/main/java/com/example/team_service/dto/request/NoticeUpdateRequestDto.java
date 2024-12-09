package com.example.team_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeUpdateRequestDto{
    @NotBlank(message = "공지 제목은 필수입니다.")
    private String title;   // 수정할 공지 제목

    @NotBlank(message = "공지 내용은 필수입니다.")
    private String content; // 수정할 공지 내용
}
