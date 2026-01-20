package com.example.speakOn.domain.myReport.dto.response;

import com.example.speakOn.domain.myReport.enums.ToneType;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import io.swagger.v3.oas.annotations.media.Schema;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "리포트 상세 정보 응답")
    public static class ReportDetailDTO {
        @Schema(description = "리포트 기본 정보")
        private SessionSummaryDTO sessionSummary;

        @Schema(description = "AI 인사이트 카드")
        private AiInsightCardDTO aiInsight;

        @Schema(description = "사용자 회고")
        private String userReflection;

        @Schema(description = "대화 로그")
        private List<MessageLogDTO> conversationLog;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionSummaryDTO {
        private String avatarName;
        private String avatarImgUrl;
        private String job;
        private String situation;
        private Integer totalTime;
        private Integer sentenceCount;
        private Integer userDifficulty;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiInsightCardDTO {
        private String aiSummary; // 핵심 해석
        private ToneAnalysisDTO toneAnalysis;
        private List<String> aiReason; // 근거 설명
        private List<CorrectionDTO> corrections; // 교정 피드백
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToneAnalysisDTO {
        private ToneType userTone;
        private ToneType expectedTone;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorrectionDTO {
        private String original;
        private String corrected;
        private String reason;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageLogDTO {
        private SenderRole senderRole;
        private String content;
        private String audioUrl;
        private LocalDateTime createdAt;
    }
}