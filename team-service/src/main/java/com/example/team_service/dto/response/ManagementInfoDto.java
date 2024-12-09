package com.example.team_service.dto.response;

import com.example.team_service.entity.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ManagementInfoDto {
    //팀 관리 페이지에서 사용
    private Long userId;
    private String userName;
    private String contactEmail;
    private String phoneNumber;
    private String profileImage;
    private TeamMember.Role role;

/*    public ManagementInfoDto(Long userId, String userName, String contactEmail, String phoneNumber, String profileImage, TeamMember.Role role) {
        this.userId = userId;
        this.userName = userName;
        this.contactEmail = contactEmail;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.role = role.name(); // Enum을 String으로 변환
    }*/
}
