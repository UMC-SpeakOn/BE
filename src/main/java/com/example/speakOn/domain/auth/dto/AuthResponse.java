package com.example.speakOn.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponse {


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "소셜 로그인 응답 정보")
    public static class SocialLoginResponseDTO {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;

        @Schema(description = "Access Token", example = "eyJhbGciOiJIUz...")
        private String accessToken;

        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUz...")
        private String refreshToken;

    }

}
