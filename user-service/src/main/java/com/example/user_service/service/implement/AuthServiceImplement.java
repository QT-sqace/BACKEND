package com.example.user_service.service.implement;

import com.example.user_service.common.CertificationNumber;
import com.example.user_service.dto.request.auth.CheckCertificationRequestDto;
import com.example.user_service.dto.request.auth.EmailCertificationRequestDto;
import com.example.user_service.dto.request.auth.SignInRequestDto;
import com.example.user_service.dto.request.auth.SignUpRequestDto;
import com.example.user_service.dto.response.ResponseDto;
import com.example.user_service.dto.response.auth.CheckCertificationResponseDto;
import com.example.user_service.dto.response.auth.EmailCertificationResponseDto;
import com.example.user_service.dto.response.auth.SignInResponseDto;
import com.example.user_service.dto.response.auth.SignUpResponseDto;
import com.example.user_service.entity.Certification;
import com.example.user_service.entity.User;
import com.example.user_service.provider.EmailProvider;
import com.example.user_service.provider.JwtProvider;
import com.example.user_service.repository.CertificationRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;

    private final EmailProvider emailProvider;
    private final JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //강의는 userId로 중복 체크 진행함

    //인증번호 db 저장
    @Override
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto) {

        try {
            String email = dto.getEmail();

            String certificationNumber = CertificationNumber.getCertificationNumber();

            boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);
            if (!isSuccessed) return EmailCertificationResponseDto.mailSendFail();  //

            //기존의 인증번호가 있으면 삭제
            certificationRepository.deleteByEmail(email);

            //새로운 인증번호 저장
            Certification certificationEntity = new Certification(null, email, certificationNumber);
            certificationRepository.save(certificationEntity);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return EmailCertificationResponseDto.success();

    }

    //인증 번호 확인
    @Override
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto) {
        try {

            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            Certification certificationEntity = certificationRepository.findByEmail(email);
            if (certificationEntity == null) return CheckCertificationResponseDto.certificationFail();

            boolean isMatched = certificationEntity.getEmail().equals(email)
                    && certificationEntity.getCertificationNumber().equals(certificationNumber);
            if (!isMatched) return CheckCertificationResponseDto.certificationFail();



        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
        return CheckCertificationResponseDto.success();
    }

    //회원 이메일 가입
    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto) {
        try {

            //이미 존재하는 회원인지 이메일로 확인
            String email = dto.getEmail();
            boolean isExistEmail = userRepository.existsByEmail(email);
            if (isExistEmail) return SignUpResponseDto.duplicateEmail();

            String password = dto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            dto.setPassword(encodedPassword);

            //회원 정보 저장 user엔티티 참고
            User userEntity = new User(dto);
            userRepository.save(userEntity);

            //저장 후에는 인증번호 삭제
            certificationRepository.deleteByEmail(email);

        } catch (Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();

        }
        return SignUpResponseDto.success();
    }

    //회원 이메일 로그인
    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {

        String token = null;
        try {

            //이메일로 존재하는지 확인
            String email = dto.getEmail();
            User userEntity = userRepository.findByEmail(email);
            if (userEntity == null) SignInResponseDto.signInFail();

            //비밀번호 비교
            String password = dto.getPassword();
            String encodedPassword = userEntity.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if (!isMatched) return SignInResponseDto.signInFail();

            //토큰 생성하기
            Long userId = userEntity.getUserId();
            //나중에 로그인후 토큰 오류나면 여기 확인하기
            token = jwtProvider.create(String.valueOf(userId));


        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignInResponseDto.success(token);
    }
}
