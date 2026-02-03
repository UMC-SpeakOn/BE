package com.example.speakOn.domain.user.converter;

import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.entity.User;
import java.time.format.DateTimeFormatter;

public class UserConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static UserResponse.MyPageResponseDTO toMyPageResponseDTO(User user) {

        return UserResponse.MyPageResponseDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .name(user.getName())
                .email(user.getEmail())
                .socialType(user.getSocialType())
                .createdAt(user.getCreatedAt().format(DATE_FORMATTER))
                .build();
    }
}
