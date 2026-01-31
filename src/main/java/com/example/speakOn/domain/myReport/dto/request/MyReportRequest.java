package com.example.speakOn.domain.myReport.dto.request;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myRole.enums.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class MyReportRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class WriteReflectionDTO {
        @Schema(description = "사용자 소감 (120자 이내)", example = "생각보다 말이 잘 안 나왔지만, 다음엔 더 잘할 수 있을 것 같다.")
        @NotBlank(message = "소감 내용은 필수입니다.")
        @Size(max = 120, message = "소감은 120자 이내로 작성해야 합니다.")
        private String feedback;

        @Schema(description = "사용자가 수정한 체감 난이도 (1~5)", example = "3")
        @NotNull(message = "난이도는 필수입니다.")
        @Min(value = 1, message = "난이도는 최소 1 이상이어야 합니다.")
        @Max(value = 5, message = "난이도는 최대 5 이하여야 합니다.")
        private Integer difficulty;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReportFilterDTO {
        @Schema(description = "조회 시작일", example = "2024-01-01")
        private LocalDate startDate;

        @Schema(description = "조회 종료일", example = "2024-01-31")
        private LocalDate endDate;

        @Schema(description = "조회할 직무 ", example = "MARKETING")
        private JobType job;

        @Schema(description = "조회할 상황", example = "INTERVIEW")
        private SituationType situation;
    }
}