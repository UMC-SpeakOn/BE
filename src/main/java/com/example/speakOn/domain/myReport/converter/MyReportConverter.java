package com.example.speakOn.domain.myReport.converter;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.entity.ConversationCorrection;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import org.springframework.data.domain.Page;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class MyReportConverter {

    // 소감 작성 결과
    public static MyReportResponseDTO.WriteReflectionResultDTO toWriteReflectionResultDTO(MyReport myReport) {
        return MyReportResponseDTO.WriteReflectionResultDTO.builder()
                .reportId(myReport.getId())
                .reflection(myReport.getUserReflection())
                .difficulty(myReport.getDifficulty())
                .updatedAt(myReport.getUpdatedAt() != null ? myReport.getUpdatedAt() : myReport.getCreatedAt())
                .build();
    }

    // 리포트 목록 조회
    public static MyReportResponseDTO.ReportSummaryDTO toReportSummaryDTO(MyReport myReport) {
        ConversationSession session = myReport.getSession();
        MyRole myRole = (session != null) ? session.getMyRole() : null;

        String jobName = (myRole != null && myRole.getJob() != null) ? myRole.getJob().name() : "UNKNOWN";
        String situationName = (myRole != null && myRole.getSituation() != null) ? myRole.getSituation().name() : "UNKNOWN";

        return MyReportResponseDTO.ReportSummaryDTO.builder()
                .reportId(myReport.getId())
                .createdAt(myReport.getCreatedAt().toLocalDate())
                .situation(situationName)
                .job(jobName)
                .userReflection(myReport.getUserReflection())
                .difficulty(myReport.getDifficulty())
                .build();
    }

    public static MyReportResponseDTO.ReportSummaryListDTO toReportSummaryListDTO(Page<MyReport> reportPage) {
        List<MyReportResponseDTO.ReportSummaryDTO> reportList = reportPage.stream()
                .map(MyReportConverter::toReportSummaryDTO)
                .collect(Collectors.toList());

        return MyReportResponseDTO.ReportSummaryListDTO.builder()
                .reportList(reportList)
                .listSize(reportList.size())
                .totalPage(reportPage.getTotalPages())
                .totalElements(reportPage.getTotalElements())
                .isFirst(reportPage.isFirst())
                .isLast(reportPage.isLast())
                .build();
    }

    // 리포트 상세 조회
    public static MyReportResponseDTO.ReportDetailDTO toReportDetailDTO(MyReport myReport) {
        ConversationSession session = myReport.getSession();
        MyRole myRole = (session != null) ? session.getMyRole() : null;
        Avatar avatar = (myRole != null) ? myRole.getAvatar() : null;

        LocalTime totalTime = LocalTime.of(0, 0, 0);
        if (session != null && session.getTotalTime() != null) {
            totalTime = LocalTime.ofSecondOfDay(session.getTotalTime());
        }

        return MyReportResponseDTO.ReportDetailDTO.builder()
                .reportId(myReport.getId())
                .sessionSummary(MyReportResponseDTO.SessionSummaryDTO.builder()
                        .totalTime(totalTime)
                        .sentenceCount(session != null ? session.getSentenceCount() : 0)
                        .difficulty(myReport.getDifficulty())
                        .createdAt(myReport.getCreatedAt().toLocalDate())
                        .job((myRole != null && myRole.getJob() != null) ? myRole.getJob().name() : "-")
                        .situation((myRole != null && myRole.getSituation() != null) ? myRole.getSituation().name() : "-")
                        .avatarName(avatar != null ? avatar.getName() : "AI")
                        .avatarImgUrl(avatar != null ? avatar.getImgUrl() : "")
                        .build())
                .aiInsightCard(MyReportResponseDTO.AiInsightCardDTO.builder()
                        .aiSummary(myReport.getAiSummary())
                        .aiReason(myReport.getAiReason())
                        .toneAnalysis(myReport.getConversationTone() != null ?
                                toToneAnalysisDTO(myReport) : null)
                        .corrections(toCorrectionDTOList(myReport.getCorrections()))
                        .build())
                .userReflection(myReport.getUserReflection())
                .conversationLog(List.of())
                .build();
    }

    // 톤 분석
    private static MyReportResponseDTO.ToneAnalysisDTO toToneAnalysisDTO(MyReport myReport) {
        var tone = myReport.getConversationTone();
        return MyReportResponseDTO.ToneAnalysisDTO.builder()
                .userTone(tone.getUserTone())
                .expectedTone(tone.getExpectedTone())
                .build();
    }

    // 교정 리스트
    private static List<MyReportResponseDTO.CorrectionDTO> toCorrectionDTOList(List<ConversationCorrection> corrections) {
        if (corrections == null) return List.of();

        return corrections.stream()
                .map(c -> MyReportResponseDTO.CorrectionDTO.builder()
                        .original(c.getOriginalContent())
                        .corrected(c.getCorrectedContent())
                        .reason(c.getCorrectionReason())
                        .build())
                .collect(Collectors.toList());
    }

    // 대화 로그 조회
    public static MyReportResponseDTO.MessageLogListDTO toMessageLogListDTO(Long reportId, List<ConversationMessage> messages) {
        List<MyReportResponseDTO.MessageLogDTO> logList = messages.stream()
                .map(msg -> MyReportResponseDTO.MessageLogDTO.builder()
                        .messageId(msg.getId())
                        .senderRole(msg.getSenderRole())
                        .content(msg.getContent())
                        .audioUrl(msg.getAudioUrl())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return MyReportResponseDTO.MessageLogListDTO.builder()
                .reportId(reportId)
                .messages(logList)
                .totalMessageCount(logList.size())
                .build();
    }
}