package com.example.user_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "user_info")
@Table(name = "user_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    private Long userId;

    @OneToOne
    @MapsId //User엔티티의 userId를 기본 키로 사용
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String userName;    //이름
    private String address;     //주소
    private String phoneNumber; //전화번호

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserInfo(User user, String userName, LocalDateTime createdAt) {
        this.user = user;
        this.userName = userName;
        this.createdAt = createdAt;
    }
}
