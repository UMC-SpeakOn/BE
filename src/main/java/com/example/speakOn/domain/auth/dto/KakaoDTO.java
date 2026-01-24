package com.example.speakOn.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class KakaoDTO {

    /**
     * 카카오 인가 코드 요청 DTO
     */
    @Schema(description = "카카오 인가 코드 요청")
    public record CodeRequest(
            @Schema(description = "카카오 인가 코드 (리다이렉트 URL의 code 파라미터)", example = "aBcDeFgHiJkLmNoPqRsTuVwXyZ123456789")
            String code
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
