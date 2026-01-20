package com.example.speakOn.domain.myReport.service;

import com.example.speakOn.domain.myReport.code.MyReportErrorCode;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.repository.MyReportRepository;
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
     * 날짜, 직무, 상황별로 정렬 및 필터링하여 조회
     */
    public MyReportResponseDTO.ReportSummaryListDTO getReportList(User user, JobType job, SituationType situation) {
        List<MyReport> reports = myReportRepository.findAllByUserAndFilters(user, job, situation);

        List<MyReportResponseDTO.ReportSummaryDTO> summaryDTOs = reports.stream()
                .map(report -> MyReportResponseDTO.ReportSummaryDTO.builder()
                        .reportId(report.getId())
                        .job(report.getSession().getMyRole().getJob().name())
                        .situation(report.getSession().getMyRole().getSituation().name())
                        .userReflection(report.getUserReflection()) // 소감을 리스트 전면에 배치
                        .createdAt(report.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return MyReportResponseDTO.ReportSummaryListDTO.builder()
                .reportList(summaryDTOs)
                .build();
    }

    /**
     * 리포트 상세 조회
     * 특정 리포트의 요약 정보, AI 인사이트, 대화 로그 모두 조회
     */
    public MyReportResponseDTO.ReportDetailDTO getReportDetail(Long reportId, User user) {
        // 리포트 존재 여부 확인
        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new ErrorHandler(MyReportErrorCode.REPORT_NOT_FOUND));

        // 권한 확인
        if (user == null || user.getId() == null ||
                !report.getSession().getMyRole().getUser().getId().equals(user.getId())) {
            log.warn("권한 없는 리포트 접근 시도 - reportId: {}, userId: {}", reportId, user != null ? user.getId() : "null");
            throw new ErrorHandler(MyReportErrorCode.REPORT_ACCESS_DENIED);
        }

        // 해당 세션의 전체 대화 로그 조회
        List<ConversationMessage> messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(report.getSession());

        return MyReportResponseDTO.ReportDetailDTO.builder()
                .sessionSummary(MyReportResponseDTO.SessionSummaryDTO.builder()
                        .avatarName(report.getSession().getMyRole().getAvatar().getName())
                        .avatarImgUrl(report.getSession().getMyRole().getAvatar().getImgUrl())
                        .job(report.getSession().getMyRole().getJob().name())
                        .situation(report.getSession().getMyRole().getSituation().name())
                        .totalTime(report.getSession().getTotalTime())
                        .sentenceCount(report.getSession().getSentenceCount())
                        .userDifficulty(report.getSession().getUserDifficulty())
                        .createdAt(report.getCreatedAt())
                        .build())
                .aiInsight(MyReportResponseDTO.AiInsightCardDTO.builder()
                        .aiSummary(report.getAiSummary())
                        .toneAnalysis(MyReportResponseDTO.ToneAnalysisDTO.builder()
                                .userTone(report.getConversationTone() != null ? report.getConversationTone().getUserTone() : null)
                                .expectedTone(report.getConversationTone() != null ? report.getConversationTone().getExpectedTone() : null)
                                .build())
                        .aiReason(report.getAiReason())
                        .corrections(report.getCorrections().stream()
                                .map(c -> MyReportResponseDTO.CorrectionDTO.builder()
                                        .original(c.getOriginalContent())
                                        .corrected(c.getCorrectedContent())
                                        .reason(c.getCorrectionReason())
                                        .build())
                                .collect(Collectors.toList()))
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