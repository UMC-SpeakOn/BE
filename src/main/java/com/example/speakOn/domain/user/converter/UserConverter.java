package com.example.speakOn.domain.user.converter;

import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.entity.User;

public class UserConverter {

    public static UserResponse.MyPageResponseDTO toMyPageResponseDTO(User user) {

        return UserResponse.MyPageResponseDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .name(user.getName())
                .email(user.getEmail())
                .socialType(user.getSocialType())
                .build();
    }
}
