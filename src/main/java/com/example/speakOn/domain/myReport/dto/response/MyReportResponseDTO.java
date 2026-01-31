package com.example.speakOn.domain.myReport.dto.response;

import com.example.speakOn.domain.myReport.enums.ToneType;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MyReportResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "리포트 목록 응답 (페이징 포함)")
    public static class ReportSummaryListDTO {
        @Schema(description = "리포트 요약 목록")
        private List<ReportSummaryDTO> reportList;

        @Schema(description = "현재 페이지의 리스트 크기", example = "10")
        private Integer listSize;

        @Schema(description = "총 페이지 수", example = "5")
        private Integer totalPage;

        @Schema(description = "총 데이터 개수", example = "42")
        private Long totalElements;

        @Schema(description = "첫 페이지 여부", example = "true")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지 여부", example = "false")
        private Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "리포트 요약 정보 (목록 조회용)")
    public static class ReportSummaryDTO {
        @Schema(description = "리포트 ID", example = "1")
        private Long reportId;

        @Schema(description = "직무", example = "PM")
        private String job;

        @Schema(description = "상황", example = "INTERVIEW")
        private String situation;

        @Schema(description = "사용자 소감", example = "아쉬움이 남는 대화였다.")
        private String userReflection;

        @Schema(description = "생성 날짜", example = "2024-01-27")
        private LocalDate createdAt;

        @Schema(description = "난이도 (1~5)", example = "3")
        private Integer difficulty;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "리포트 상세 정보 응답")
    public static class ReportDetailDTO {
        @Schema(description = "리포트 ID")
        private Long reportId;

        @Schema(description = "리포트 기본 정보 (세션 요약)")
        private SessionSummaryDTO sessionSummary;

        @Schema(description = "AI 인사이트 카드")
        private AiInsightCardDTO aiInsightCard;

        @Schema(description = "사용자 회고")
        private String userReflection;

        @Schema(description = "대화 로그")
        private List<MessageLogDTO> conversationLog;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "세션 요약 정보")
    public static class SessionSummaryDTO {
        private String avatarName;
        private String avatarImgUrl;
        private String job;
        private String situation;

        @Schema(description = "총 소요 시간", example = "00:15:00")
        private LocalTime totalTime;

        @Schema(description = "총 문장 수", example = "25")
        private Integer sentenceCount;

        @Schema(description = "체감 난이도 (수정된 값)", example = "3")
        private Integer difficulty;

        private LocalDate createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiInsightCardDTO {
        private String aiSummary;
        private ToneAnalysisDTO toneAnalysis;
        private List<String> aiReason;
        private List<CorrectionDTO> corrections;
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

        @Schema(description = "대화 시각", example = "2024-01-23T14:30:00")
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "소감 작성 및 난이도 수정 결과")
    public static class WriteReflectionResultDTO {
        @Schema(description = "리포트 ID")
        Long reportId;

        @Schema(description = "수정된 난이도")
        Integer difficulty;

        @Schema(description = "작성된 소감")
        String reflection;

        @Schema(description = "수정 완료 시각")
        LocalDateTime updatedAt;
    }
}