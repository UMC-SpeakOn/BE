package com.example.speakOn.domain.auth.converter;

import com.example.speakOn.domain.auth.dto.AuthResponse;
import com.example.speakOn.domain.auth.dto.KakaoDTO;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.enums.Role;
import com.example.speakOn.domain.user.enums.SocialType;

public class AuthConverter {

    // 로그인 응답 DTO 변환
    public static AuthResponse.SocialLoginResponseDTO toSocialLoginResponseDTO(Long userId, String accessToken, String refreshToken) {
        return AuthResponse.SocialLoginResponseDTO.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 카카오 유저 정보 -> User 엔티티 변환
    public static User toUser(KakaoDTO.UserInfoResponse kakaoUserInfo, SocialType socialType) {
        return User.builder()
                .socialId(String.valueOf(kakaoUserInfo.id()))
                .socialType(socialType)
                .email(kakaoUserInfo.kakaoAccount().email())
                .name(kakaoUserInfo.kakaoAccount().profile().nickname())
                .nickname(kakaoUserInfo.kakaoAccount().profile().nickname())
                .role(Role.USER)
                .isOnboarded(false)
                .build();
    }
}
