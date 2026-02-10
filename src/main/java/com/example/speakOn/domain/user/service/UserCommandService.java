package com.example.speakOn.domain.user.service;

import com.example.speakOn.domain.user.dto.UserRequest;
import com.example.speakOn.domain.user.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandService {

    /**
     * 사용자 프로필 정보를 수정
     * (닉네임, 프로필 이미지)
     */
    UserResponse.UpdateProfileResponseDTO updateProfile(
            Long userId,
            UserRequest.UpdateProfileRequestDto request
    );

    /**
     * 회원 탈퇴 (Hard Delete)
     */
    UserResponse.WithdrawResponseDTO withdrawUser(Long userId);
}
