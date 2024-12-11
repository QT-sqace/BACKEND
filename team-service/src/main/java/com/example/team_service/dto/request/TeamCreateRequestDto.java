package com.example.team_service.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class TeamCreateRequestDto {
    private String projectName;        //워크스페이스 이름
    private String password;            //팀 비밀번호
    private MultipartFile projectImage;
    private List<String> emails;       //초대받을 이메일 리스트
}