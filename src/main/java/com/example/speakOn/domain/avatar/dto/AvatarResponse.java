package com.example.speakOn.domain.avatar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AvatarResponse {

    @Schema(description = "아바타 목록 응답")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvatarListItem {

        @Schema(description = "아바타 ID", example = "1")
        private Long id;

        @Schema(description = "아바타 이름", example = "Emma")
        private String name;

        @Schema(description = "아바타 프로필 이미지 URL", example = "https://example.com/avatar/emma.jpg")
        private String imgUrl;

        @Schema(description = "아바타 국가", example = "United States")
        private String nationality;

        @Schema(description = "아바타 나이", example = "28")
        private Integer age;
    }
}
