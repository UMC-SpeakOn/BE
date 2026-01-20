package com.example.speakOn.domain.myReport.dto.response;

import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

public class MyReportResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSummaryListDTO {
        private List<ReportSummaryDTO> reportList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSummaryDTO {
        private Long reportId;
        private String job;
        private String situation;
        private String userReflection;
        private LocalDateTime createdAt;
    }
}