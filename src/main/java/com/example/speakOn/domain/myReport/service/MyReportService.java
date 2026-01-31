package com.example.speakOn.domain.myReport.service;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.code.MyReportErrorCode;
import com.example.speakOn.domain.myReport.converter.MyReportConverter;
import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.entity.ConversationCorrection;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.exception.MyReportException;
import com.example.speakOn.domain.myReport.repository.ConversationCorrectionRepository;
import com.example.speakOn.domain.myReport.repository.MyReportRepository;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.mySpeak.repository.ConversationSessionRepository;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.ai.service.AiAnalysisService;
import com.example.speakOn.global.ai.service.AiSpeakService;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyReportService {

    private final MyReportRepository myReportRepository;
    private final ConversationMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationSessionRepository sessionRepository;
    private final AiSpeakService aiSpeakService;
    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper;
    private final ConversationCorrectionRepository correctionRepository;

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    /**
     * 리포트 목록 조회
     */
    public MyReportResponseDTO.ReportSummaryListDTO getReportList(Long userId, JobType job, SituationType situation) {
        User user = findUser(userId);

        List<MyReport> reports = myReportRepository.findAllByUserAndFilters(user, job, situation);
        Page<MyReport> reportPage = new PageImpl<>(reports);

        return MyReportConverter.toReportSummaryListDTO(reportPage);
    }

    /**
     * 리포트 상세 조회
     */
    public MyReportResponseDTO.ReportDetailDTO getReportDetail(Long reportId, Long userId) {
        User user = findUser(userId);

        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        List<ConversationMessage> messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(report.getSession());

        validateReportOwner(report, user);
        return MyReportConverter.toReportDetailDTO(report, messages);
    }

    /**
     * 대화 로그 상세 조회
     */
    public MyReportResponseDTO.MessageLogListDTO getConversationLogs(Long reportId, Long userId) {
        User user = findUser(userId);
        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);

        ConversationSession session = report.getSession();
        List<ConversationMessage> messages = (session != null)
                ? messageRepository.findAllBySessionOrderByCreatedAtAsc(session)
                : List.of();

        return MyReportConverter.toMessageLogListDTO(reportId, messages);
    }

    /**
     * 사용자 소감 작성 및 난이도 수정
     */
    @Transactional
    public MyReportResponseDTO.WriteReflectionResultDTO writeReflection(Long reportId, MyReportRequest.WriteReflectionDTO request, Long userId) {
        User user = findUser(userId);

        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);

        report.updateReflectionAndDifficulty(request.getFeedback(), request.getDifficulty());

        return MyReportConverter.toWriteReflectionResultDTO(report);
    }

    private void validateReportOwner(MyReport report, User user) {
        ConversationSession session = report.getSession();
        User reportOwner = (session != null && session.getMyRole() != null)
                ? session.getMyRole().getUser()
                : null;
      
        if (reportOwner == null || !reportOwner.getId().equals(user.getId())) {
            log.warn("권한 없는 리포트 접근 시도 - reportId: {}, userId: {}", report.getId(), user.getId());
            throw new MyReportException(MyReportErrorCode.REPORT_ACCESS_DENIED);
        }
    }

    /**
     * 리포트 상세 정보 생성 및 조회
     */
    @Transactional
    public MyReportResponseDTO.ReportDetailDTO generateReport(Long sessionId) {
        // 1. 데이터 조회 (팀원의 Repository 방식 준수: null 체크 직접 수행)
        ConversationSession session = sessionRepository.findById(sessionId);
        if (session == null) {
            throw new GeneralException(ErrorStatus.SESSION_NOT_FOUND);
        }

        List<ConversationMessage> messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(session);
        if (messages.isEmpty()) {
            throw new GeneralException(ErrorStatus.CONVERSATION_NOT_FOUND);
        }

        // 2. AI 분석 수행
        MyReportResponseDTO.AiInsightCardDTO aiInsightCard = getAiInsight(messages);

        // 3. AI 분석 결과 DB 저장 로직
        // session.getMyReport()가 없으면 새로 생성, 있으면 업데이트
        MyReport myReport = session.getMyReport();
        if (myReport == null) {
            myReport = MyReport.builder()
                    .session(session)
                    .aiSummary(aiInsightCard.getAiSummary())
                    .aiReason(aiInsightCard.getAiReason())
                    .difficulty(session.getUserDifficulty())
                    .build();
            myReportRepository.save(myReport);
        }

        MyReport finalReport = myReport;
        List<ConversationCorrection> corrections = aiInsightCard.getCorrections().stream()
                .map(dto -> ConversationCorrection.builder()
                        .report(finalReport)
                        .originalContent(dto.getOriginal())
                        .correctedContent(dto.getCorrected())
                        .correctionReason(dto.getReason())
                        .build())
                .collect(Collectors.toList());

        correctionRepository.saveAll(corrections);

        // 4. 최종 DTO 조립
        return MyReportResponseDTO.ReportDetailDTO.builder()
                .reportId(myReport.getId()) // 저장된 리포트 ID 사용
                .sessionSummary(buildSessionSummary(session, messages))
                .aiInsightCard(aiInsightCard)
                .userReflection(myReport.getUserReflection())
                .conversationLog(buildMessageLogs(messages))
                .build();
    }

    /**
     * AI 분석 카드 생성 로직
     */
    private MyReportResponseDTO.AiInsightCardDTO getAiInsight(List<ConversationMessage> messages) {
        String transcript = messages.stream()
                .map(m -> String.format("[%s]: %s", m.getSenderRole(), m.getContent()))
                .collect(Collectors.joining("\n"));

        String aiJsonResponse = aiAnalysisService.getAnalysisResult(transcript);

        try {

            String cleanedJson = aiJsonResponse.replaceAll("(?s)```json\\s*|```", "").trim();
            return objectMapper.readValue(cleanedJson, MyReportResponseDTO.AiInsightCardDTO.class);
        } catch (JsonProcessingException e) {
            log.error("AI JSON Parsing Error. Raw Response: {}", aiJsonResponse);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 세션 요약 정보 조립 (중복 빌더 제거 및 MyRole 참조 최적화)
     */
    private MyReportResponseDTO.SessionSummaryDTO buildSessionSummary(ConversationSession session, List<ConversationMessage> messages) {
        LocalTime totalTime = (session.getTotalTime() != null)
                ? LocalTime.ofSecondOfDay(session.getTotalTime())
                : LocalTime.MIDNIGHT;

        MyRole myRole = session.getMyRole();
        MyReport myReport = session.getMyReport();

        return MyReportResponseDTO.SessionSummaryDTO.builder()
                .avatarName(myRole.getAvatar().getName())
                .avatarImgUrl(myRole.getAvatar().getImgUrl())
                .job(myRole.getJob().name())
                .situation(myRole.getSituation().name())
                .totalTime(totalTime)
                .sentenceCount(session.getSentenceCount())
                .difficulty(myReport != null ? myReport.getDifficulty() : session.getUserDifficulty())
                .createdAt(session.getStartedAt().toLocalDate())
                .build();
    }

    /**
     * 대화 로그 리스트 조립
     */
    private List<MyReportResponseDTO.MessageLogDTO> buildMessageLogs(List<ConversationMessage> messages) {
        return messages.stream()
                .map(m -> MyReportResponseDTO.MessageLogDTO.builder()
                        .messageId(m.getId())
                        .senderRole(m.getSenderRole())
                        .content(m.getContent())
                        .audioUrl(m.getAudioUrl())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}