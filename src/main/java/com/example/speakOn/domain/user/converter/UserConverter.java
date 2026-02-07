package com.example.speakOn.domain.user.converter;

import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.entity.User;
import java.time.LocalDateTime;

public class UserConverter {

    /**
     * User 엔티티와 구독 정보를 포함한 마이페이지 응답 DTO로 변환
     */
    public static UserResponse.MyPageResponseDTO toMyPageResponseDTO(
            User user,
            Boolean isSubscribed,
            LocalDateTime subscriptionExpiredAt) {

        return UserResponse.MyPageResponseDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .name(user.getName())
                .email(user.getEmail())
                .socialType(user.getSocialType())
                .createdAt(user.getCreatedAt())
                .isSubscribed(isSubscribed)
                .subscriptionExpiredAt(subscriptionExpiredAt)
                .build();
    }
}
