package com.example.speakOn.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class KakaoDTO {

    /**
     * 카카오 인가 코드 요청 DTO
     */

    @Schema(description = "카카오 인가 코드 요청")
    public record CodeRequest(
            @Schema(description = "카카오 인가 코드 (리다이렉트 URL의 code 파라미터)", example = "aBcDeFgHiJkLmNoPqRsTuVwXyZ123456789")
            @NotBlank
            String code,

            @Schema(description = "리다이렉트 URI (프론트 환경에 맞게: localhost:5173 또는 배포 주소)", example = "http://localhost:5173")
            String redirectUri
    ) {
    }

    /**
     * 카카오 토큰 응답 DTO
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TokenResponse(
            @JsonProperty("access_token")
            String accessToken,

            @JsonProperty("refresh_token")
            String refreshToken
    ) {
    }

    /**
     * 카카오 사용자 정보 응답 DTO
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserInfoResponse(
            Long id,

            @JsonProperty("kakao_account")
            KakaoAccount kakaoAccount
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(
            String email,
            Profile profile
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
            String nickname
    ) {
    }
}
