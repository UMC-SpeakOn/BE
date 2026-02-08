package com.example.speakOn.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프로필 정보 수정 요청 DTO")
    public static class UpdateProfileRequestDto {

        @Schema(description = "변경할 닉네임 (선택사항)", example = "newNickname")
        private String nickname;

        @Schema(description = "프로필 이미지 파일 (선택사항)", type = "string", format = "binary")
        private MultipartFile profileImage;
    }
}
