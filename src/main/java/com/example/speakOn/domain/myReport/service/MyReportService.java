package com.example.speakOn.domain.myReport.service;

import com.example.speakOn.domain.myReport.code.MyReportErrorCode;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.entity.ConversationCorrection;
import com.example.speakOn.domain.myReport.repository.MyReportRepository;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyReportService {

    private final MyReportRepository myReportRepository;
    private final ConversationMessageRepository messageRepository;

    /**
     * 리포트 목록 조회
     */
    public MyReportResponseDTO.ReportSummaryListDTO getReportList(User user, JobType job, SituationType situation) {
        List<MyReport> reports = myReportRepository.findAllByUserAndFilters(user, job, situation);

        List<MyReportResponseDTO.ReportSummaryDTO> summaryDTOs = reports.stream()
                .map(report -> {
                    ConversationSession session = report.getSession();
                    MyRole myRole = (session != null) ? session.getMyRole() : null;

                    String jobName = (myRole != null && myRole.getJob() != null) ? myRole.getJob().name() : "UNKNOWN";
                    String situationName = (myRole != null && myRole.getSituation() != null) ? myRole.getSituation().name() : "UNKNOWN";

                    return MyReportResponseDTO.ReportSummaryDTO.builder()
                            .reportId(report.getId())
                            .job(jobName)
                            .situation(situationName)
                            .userReflection(report.getUserReflection())
                            .createdAt(report.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return MyReportResponseDTO.ReportSummaryListDTO.builder()
                .reportList(summaryDTOs)
                .build();
    }

    /**
     * 리포트 상세 조회
     */
    public MyReportResponseDTO.ReportDetailDTO getReportDetail(Long reportId, User user) {
        // 리포트 존재 여부 판단
        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new ErrorHandler(MyReportErrorCode.REPORT_NOT_FOUND));

        // 권한 확인
        ConversationSession session = report.getSession();
        MyRole myRole = (session != null) ? session.getMyRole() : null;
        User reportOwner = (myRole != null) ? myRole.getUser() : null;

        if (user == null || user.getId() == null ||
                reportOwner == null || reportOwner.getId() == null ||
                !reportOwner.getId().equals(user.getId())) {
            log.warn("권한 없는 리포트 접근 시도 - reportId: {}, userId: {}", reportId, user != null ? user.getId() : "null");
            throw new ErrorHandler(MyReportErrorCode.REPORT_ACCESS_DENIED);
        }

        Avatar avatar = (myRole != null) ? myRole.getAvatar() : null;

        // 해당 세션의 전체 대화 로그 조회
        List<ConversationMessage> messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(session);

        return MyReportResponseDTO.ReportDetailDTO.builder()
                .sessionSummary(MyReportResponseDTO.SessionSummaryDTO.builder()
                        .avatarName(avatar != null ? avatar.getName() : null)
                        .avatarImgUrl(avatar != null ? avatar.getImgUrl() : null)
                        .job((myRole != null && myRole.getJob() != null) ? myRole.getJob().name() : "UNKNOWN")
                        .situation((myRole != null && myRole.getSituation() != null) ? myRole.getSituation().name() : "UNKNOWN")
                        .totalTime(session != null ? session.getTotalTime() : null)
                        .sentenceCount(session != null ? session.getSentenceCount() : null)
                        .userDifficulty(session != null ? session.getUserDifficulty() : null)
                        .createdAt(report.getCreatedAt())
                        .build())
                .aiInsight(MyReportResponseDTO.AiInsightCardDTO.builder()
                        .aiSummary(report.getAiSummary())
                        .toneAnalysis(MyReportResponseDTO.ToneAnalysisDTO.builder()
                                .userTone(report.getConversationTone() != null ? report.getConversationTone().getUserTone() : null)
                                .expectedTone(report.getConversationTone() != null ? report.getConversationTone().getExpectedTone() : null)
                                .build())
                        .aiReason(report.getAiReason())
                        .corrections(
                                // 리스트 NPE 방지
                                (report.getCorrections() != null ? report.getCorrections() : List.<ConversationCorrection>of())
                                        .stream()
                                        .map(c -> MyReportResponseDTO.CorrectionDTO.builder()
                                                .original(c.getOriginalContent())
                                                .corrected(c.getCorrectedContent())
                                                .reason(c.getCorrectionReason())
                                                .build())
                                        .collect(Collectors.toList())
                        )
                        .build())
                .userReflection(report.getUserReflection())
                .conversationLog(messages.stream()
                        .map(m -> MyReportResponseDTO.MessageLogDTO.builder()
                                .senderRole(m.getSenderRole())
                                .content(m.getContent())
                                .audioUrl(m.getAudioUrl())
                                .createdAt(m.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}