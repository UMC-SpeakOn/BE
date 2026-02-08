package com.example.speakOn.domain.user.dto;

import com.example.speakOn.domain.user.enums.SocialType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

        @Schema(description = "서비스 가입일", example = "2026-01-24T20:56:20.663238")
        private LocalDateTime createdAt;

        @Schema(description = "구독 여부", example = "true")
        private Boolean isSubscribed;

        @Schema(description = "구독 만료일", example = "2026-01-24T20:56:20.663238")
        private LocalDateTime subscriptionExpiredAt;

        @Schema(description = "구독 해지 여부", example = "false")
        private Boolean isSubscriptionCancelled;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "프로필 수정 응답 DTO")
    public static class UpdateProfileResponseDTO {

        @Schema(description = "유저 ID", example = "1")
        private Long userId;

        @Schema(description = "변경된 닉네임", example = "newNickname")
        private String nickname;

        @Schema(description = "변경된 프로필 이미지 URL", example = "https://bucket.s3.region.amazonaws.com/profile/uuid_filename.jpg")
        private String profileImgUrl;

        @Schema(description = "수정 완료 메시지", example = "프로필이 성공적으로 수정되었습니다.")
        private String message;
    }
}
