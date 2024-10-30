package com.example.user_service.entity;

import com.example.user_service.dto.request.auth.SignUpRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "user")
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String email;
    @Column(nullable = true)    //소셜 로그인시 password = null
    private String password;
    private String provider;    //제공자 email, kakao, google
    private String providerId;  //kakao, google에서 제공되는 id
    private String role;

    //이메일 회원가입시 사용
    public User(SignUpRequestDto dto) {
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.provider = "email";
        this.providerId = null;
        this.role = "ROLE_USER";
    }

    //소셜 로그인시 사용
    public User (String providerId, String email, String type) {
        this.email = email;
        this.password = null;
        this.provider = type;
        this.providerId = providerId;
        this.role = "ROLE_USER";
    }
}
