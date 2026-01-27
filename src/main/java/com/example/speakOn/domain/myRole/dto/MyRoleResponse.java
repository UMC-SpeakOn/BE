package com.example.speakOn.domain.myRole.dto;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MyRoleResponse {

    @Schema(description = "롤 추가 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMyRoleResultDTO {

        @Schema(description = "생성된 MyRole ID", example = "1")
        private Long myRoleId;

        @Schema(description = "선택한 아바타 ID", example = "1")
        private Long avatarId;

        @Schema(description = "아바타 이름", example = "Emily")
        private String avatarName;

        @Schema(description = "선택한 직무", example = "MARKETING")
        private JobType job;

        @Schema(description = "선택한 상황", example = "INTERVIEW")
        private SituationType situation;
    }

    @Schema(description = "롤 삭제 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteMyRoleResultDTO {

        @Schema(description = "삭제된 MyRole ID", example = "1")
        private Long myRoleId;

        @Schema(description = "삭제 성공 메시지", example = "롤이 성공적으로 삭제되었습니다.")
        private String message;
    }
}