package com.example.speakOn.domain.user.service;

import com.example.speakOn.domain.user.converter.UserConverter;
import com.example.speakOn.domain.user.dto.UserRequest;
import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import com.example.speakOn.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Override
    @Transactional
    public UserResponse.UpdateProfileResponseDTO updateProfile(
            Long userId,
            UserRequest.UpdateProfileRequestDto request) {

        // 1. 입력값 검증 - 닉네임이나 프로필 이미지 중 최소 1개는 필수
        if ((request.getNickname() == null || request.getNickname().trim().isEmpty()) &&
            (request.getProfileImage() == null || request.getProfileImage().isEmpty())) {
            throw new ErrorHandler(ErrorStatus.PROFILE_UPDATE_FAILED);
        }

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 3. 기존 프로필 이미지 URL 저장 (삭제용)
        String oldProfileImgUrl = user.getProfileImgUrl();

        // 4. 닉네임 수정
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.updateNickname(request.getNickname());
        }

        // 5. 프로필 이미지 수정
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            // 새로운 이미지 S3 업로드
            String newProfileImgUrl = s3Util.uploadFile(request.getProfileImage(), "profile");
            user.updateProfileImage(newProfileImgUrl);
            // 기존 이미지 삭제
            if (oldProfileImgUrl != null && !oldProfileImgUrl.isEmpty()) {
                try {
                    s3Util.deleteFile(oldProfileImgUrl);
                } catch (Exception e) {
                }
            }
        }

        // 6. 사용자 정보 저장
        User updatedUser = userRepository.save(user);

        // 7. 응답 DTO 반환
        return UserConverter.toUpdateProfileResponseDTO(
                updatedUser,
                "프로필이 성공적으로 수정되었습니다."
        );
    }
}
