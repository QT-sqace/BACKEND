package com.example.user_service.service;

import com.example.user_service.client.ChatServiceClient;
import com.example.user_service.dto.external.UpdateUserProfileDto;
import com.example.user_service.dto.request.auth.ProfileResponseDto;
import com.example.user_service.dto.request.auth.UpdateProfileRequestDto;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserInfo;
import com.example.user_service.repository.UserInfoRepository;
import com.example.user_service.repository.UserRepository;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final MinioClient minioClient;
    private final ChatServiceClient chatServiceClient;

    @Value("${minio.bucket.user-profile}")
    private String ProfileBucket;

    @Value("${minio.server.url}")
    private String minioUrl;

    public ProfileResponseDto getProfile(Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다"));

        User user = userInfo.getUser();

        return new ProfileResponseDto(
                userInfo.getProfileImage(),
                userInfo.getUserName(),
                userInfo.getContactEmail(),
                userInfo.getAddress(),
                userInfo.getPhoneNumber(),
                userInfo.getCompany(),
                user.getProvider()
        );
    }

    public void updateProfile(Long userId, UpdateProfileRequestDto requestDto) {
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        ensureBucketExists(ProfileBucket);
        // 기존 프로필 이미지 삭제 로직 추가
        if (userInfo.getProfileImage() != null && !userInfo.getProfileImage().isEmpty()) {
            try {
                Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder()
                        .bucket(ProfileBucket)
                        .prefix("users/" + userId + "/") // 특정 사용자의 프로필 경로
                        .build());

                for (Result<Item> result : objects) {
                    String objectName = result.get().objectName();
                    log.info("기존 프로필 이미지 삭제: {}", objectName);

                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(ProfileBucket)
                            .object(objectName)
                            .build());
                }
            } catch (Exception e) {
                log.error("기존 프로필 이미지 삭제 중 오류 발생: {}", e.getMessage());
            }
        }

        String imageUrl = null;
        if (requestDto.getProfileImage() != null && !requestDto.getProfileImage().isEmpty()) {
            log.info("이미지 변경 요청");
            try {
                //파일명 생성
                //이렇게하면 특정 파일이름이 인식안되는 문제가 있음
//                String fileName = "users/" + userId + "/profile_" + System.currentTimeMillis() + "_" + requestDto.getProfileImage().getOriginalFilename();
                String originalFileName = requestDto.getProfileImage().getOriginalFilename();
                String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_"); // 한글, 특수문자 제거
                String fileName = "users/" + userId + "/profile_" + System.currentTimeMillis() + "_" + sanitizedFileName;

                //MinIO에 파일 업로드
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(ProfileBucket)
                                .object(fileName)
                                .stream(requestDto.getProfileImage().getInputStream(), requestDto.getProfileImage().getSize(), -1)
                                .contentType(requestDto.getProfileImage().getContentType())
                                .build()
                );

                imageUrl = minioUrl + "/" + ProfileBucket + "/" + fileName;

                log.info("이미지 프로필 경로 확인: {}" , imageUrl);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + "문제가 발생하였습니다.");
            }
        }

        //여기에 페인으로 채팅방 프로필 경로 바꾸는 로직도 추가해야함
        userInfo.updateProfile(
                requestDto.getUserName(),
                requestDto.getContactEmail(),
                requestDto.getAddress(),
                requestDto.getPhoneNumber(),
                requestDto.getCompany(),
                imageUrl
        );

        userInfoRepository.save(userInfo);
        // FeignClient 호출하여 ChatService에 업데이트된 프로필 전송
        try {
            UpdateUserProfileDto updateUserProfileDto = new UpdateUserProfileDto(
                    userId,
                    requestDto.getUserName(),
                    imageUrl
            );
            chatServiceClient.updateProfile(updateUserProfileDto);
            log.info("채팅 서비스에 프로필 업데이트 요청 성공: {}", updateUserProfileDto);
        } catch (Exception e) {
            log.error("채팅 서비스에 프로필 업데이트 요청 중 오류 발생: {}", e.getMessage());
        }

    }

    public void ensureBucketExists(String bucketName) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                log.info("버킷이 존재하지 않습니다. 새로운 버킷을 생성합니다: {}", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("버킷 생성 완료: {}", bucketName);
            } else {
                log.info("버킷이 이미 존재합니다: {}", bucketName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + "의 문제가 발생했습니다.");
        }
    }


}
