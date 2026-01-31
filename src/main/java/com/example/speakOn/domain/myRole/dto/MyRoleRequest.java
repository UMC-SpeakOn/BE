package com.example.speakOn.domain.myRole.dto;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MyRoleRequest {

    @Schema(description = "롤 추가 요청 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMyRoleDTO {

        @Schema(description = "선택한 아바타 ID", example = "1")
        @NotNull(message = "avatarId는 필수입니다.")
        private Long avatarId;

        @Schema(description = "선택한 직무 (MARKETING, DEVELOPMENT, DESIGN, PLANNING, SALES, BUSINESS)", example = "MARKETING")
        @NotNull(message = "직무(job)는 필수입니다.")
        private JobType job;

        @Schema(description = "선택한 상황 (INTERVIEW, MEETING, ONE_ON_ONE_MEETING)", example = "INTERVIEW")
        @NotNull(message = "상황(situation)은 필수입니다.")
        private SituationType situation;
    }
}