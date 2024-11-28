package com.example.team_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeCreateRequestDto {
    @NotBlank(message = "공지 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "공지 내용은 필수입니다.")
    private String content;

    @NotNull(message = "작성자 ID는 필수입니다.")
    private Long teamMemberId;  // 작성자(TeamMember) ID
}
