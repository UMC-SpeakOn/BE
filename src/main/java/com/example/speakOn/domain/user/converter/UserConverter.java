package com.example.speakOn.domain.user.converter;

import com.example.speakOn.domain.user.dto.UserRequest;
import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class UserConverter {

    /**
     * 프로필 수정 요청 데이터로부터 Request DTO 생성
     */
    public static UserRequest.UpdateProfileRequestDto toUpdateProfileRequestDto(
            String nickname,
            MultipartFile profileImage) {

        return UserRequest.UpdateProfileRequestDto.builder()
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

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

    /**
     * 프로필 수정 후 응답 DTO로 변환
     */
    public static UserResponse.UpdateProfileResponseDTO toUpdateProfileResponseDTO(
            User user,
            String message) {

        return UserResponse.UpdateProfileResponseDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .message(message)
                .build();
    }
}
