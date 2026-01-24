package com.example.speakOn.domain.user.dto;

import com.example.speakOn.domain.user.enums.SocialType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "마이페이지 프로필 정보")
    public static class MyPageResponseDTO {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;

        @Schema(description = "닉네임", example = "speakOnUser")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        private String profileImgUrl;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "이메일", example = "example@naver.com")
        private String email;

        @Schema(description = "소셜 로그인 타입", example = "GOOGLE")
        private SocialType socialType;

    }

}
