package com.example.speakOn.domain.user.service;

import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.global.validation.annotation.ExistUser;

public interface UserQueryService {
    // User 존재 여부 검증
    Boolean existsUserById(Long userId);

    // 마이페이지 유저 정보 조회
    UserResponse.MyPageResponseDTO getMyPageInfo(@ExistUser Long userId);
}
