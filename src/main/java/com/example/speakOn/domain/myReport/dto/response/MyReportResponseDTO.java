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
    @Schema(description = "대화 로그 리스트 응답 DTO")
    public static class MessageLogListDTO {

        @Schema(description = "리포트 ID", example = "101")
        private Long reportId;

        @Schema(description = "총 대화 메시지 수", example = "12")
        private Integer totalMessageCount;

        @Schema(description = "대화 로그 목록")
        private List<MessageLogDTO> messages;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "개별 대화 메시지 상세 정보")
    public static class MessageLogDTO {

        @Schema(description = "메시지 ID", example = "5432")
        private Long messageId;

        @Schema(description = "발화 주체 (USER: 사용자, AI: 아바타)", example = "USER")
        private SenderRole senderRole;

        @Schema(description = "대화 내용", example = "Could you tell me about the speak-on?")
        private String content;

        @Schema(description = "오디오 파일 URL (다시 듣기용)", example = "https://s3.ap-northeast-2.amazonaws.com/speakon/audio/123.mp3")
        private String audioUrl;

        @Schema(description = "대화 시각", example = "2024-01-23T14:30:00")
        private LocalDateTime createdAt;
    }
}