package com.example.speakOn.domain.myReport.service;

import com.example.speakOn.domain.myReport.code.MyReportErrorCode;
import com.example.speakOn.domain.myReport.converter.MyReportConverter;
import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.entity.ConversationCorrection;
import com.example.speakOn.domain.myReport.entity.ConversationTone;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.entity.ReportViewHistory;
import com.example.speakOn.domain.myReport.exception.MyReportException;
import com.example.speakOn.domain.myReport.repository.ConversationCorrectionRepository;
import com.example.speakOn.domain.myReport.repository.ConversationToneRepository;
import com.example.speakOn.domain.myReport.repository.MyReportRepository;
import com.example.speakOn.domain.myReport.repository.ReportViewHistoryRepository;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.mySpeak.repository.ConversationSessionRepository;
import com.example.speakOn.domain.subscription.repository.SubscriptionRepository;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.ai.service.AiAnalysisService;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
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
    private final ConversationToneRepository toneRepository;
    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper;
    private final ConversationCorrectionRepository correctionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReportViewHistoryService reportViewHistoryService;
    private final ReportViewHistoryRepository reportViewHistoryRepository;
    private final int MAX_FREE_VIEW_COUNT = 5;

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    public MyReportResponseDTO.ReportSummaryListDTO getReportList(Long userId, MyReportRequest.ReportFilterDTO filter, Pageable pageable) {
        User user = findUser(userId);
        Slice<MyReport> reports = myReportRepository.findAllByUserAndFilters(user, filter, pageable);
        return MyReportConverter.toReportSummaryListDTOFromSlice(reports);
    }

    @Transactional
    public MyReportResponseDTO.ReportDetailDTO getReportDetail(Long reportId, Long userId, String viewUUID) {
        User user = findUser(userId);
        MyReport report = myReportRepository.findReportWithAllDetails(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);

        boolean isSubscribed = subscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now()).isPresent();
        boolean isLogLocked = true;
        boolean alreadyPaid = reportViewHistoryRepository.existsByReportAndUserAndViewUUID(report, user, viewUUID);

        if (isSubscribed || alreadyPaid) {
            isLogLocked = false;
        } else if (user.getTotalLogViewCount() < MAX_FREE_VIEW_COUNT) {
            try {
                ReportViewHistory history = ReportViewHistory.builder().report(report).user(user).viewUUID(viewUUID).build();
                reportViewHistoryService.trySaveHistory(history);
                user.incrementLogViewCount();
                isLogLocked = false;
            } catch (DataIntegrityViolationException e) {
                isLogLocked = false;
            }
        }

        List<ConversationMessage> messages = !isLogLocked ?
                messageRepository.findAllBySessionOrderByCreatedAtAsc(report.getSession()) : generateDummyMessages(report);

        return MyReportConverter.toReportDetailDTO(report, messages, isLogLocked, user.getTotalLogViewCount());
    }

    @Transactional
    public MyReportResponseDTO.MessageLogListDTO getConversationLogs(Long reportId, Long userId, String viewUUID) {
        User user = findUser(userId);
        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);

        boolean isSubscribed = subscriptionRepository.findActiveSubscriptionByUserId(userId, LocalDateTime.now()).isPresent();
        boolean isLogLocked = true;
        boolean isRefreshedRequest = reportViewHistoryRepository.existsByReportAndUserAndViewUUID(report, user, viewUUID);

        if (isSubscribed || isRefreshedRequest) {
            isLogLocked = false;
        } else if (user.getTotalLogViewCount() < MAX_FREE_VIEW_COUNT) {
            try {
                ReportViewHistory history = ReportViewHistory.builder().report(report).user(user).viewUUID(viewUUID).build();
                reportViewHistoryService.trySaveHistory(history);
                user.incrementLogViewCount();
                isLogLocked = false;
            } catch (DataIntegrityViolationException e) {
                isLogLocked = false;
            }
        }

        List<ConversationMessage> messages = !isLogLocked ?
                messageRepository.findAllBySessionOrderByCreatedAtAsc(report.getSession()) : generateDummyMessages(report);

        return MyReportConverter.toMessageLogListDTO(reportId, messages, isLogLocked, user.getTotalLogViewCount());
    }

    @Transactional
    public MyReportResponseDTO.WriteReflectionResultDTO writeReflection(Long reportId, MyReportRequest.WriteReflectionDTO request, Long userId) {
        User user = findUser(userId);
        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);
        report.updateReflectionAndDifficulty(request.getFeedback(), request.getDifficulty());
        return MyReportConverter.toWriteReflectionResultDTO(report);
    }

    @Transactional
    public MyReportResponseDTO.ReportDetailDTO generateReport(Long sessionId) {
        ConversationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SESSION_NOT_FOUND));

        List<ConversationMessage> messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(session);
        boolean hasUserMessage = messages.stream().anyMatch(m -> SenderRole.USER.equals(m.getSenderRole()));

        MyReport myReport = myReportRepository.findBySession(session)
                .orElseGet(() -> {
                    MyReportResponseDTO.AiInsightCardDTO aiInsight;
                    if (hasUserMessage) {
                        aiInsight = getAiInsight(messages, session.getMyRole());
                    } else {
                        aiInsight = MyReportResponseDTO.AiInsightCardDTO.builder()
                                .aiSummary("진행된 대화가 없어 AI 분석이 수행되지 않았습니다.")
                                .aiReason(List.of("사용자 답변이 감지되지 않았습니다."))
                                .corrections(Collections.emptyList()).build();
                    }

                    MyReport newReport = MyReport.builder()
                            .session(session).aiSummary(aiInsight.getAiSummary())
                            .aiReason(aiInsight.getAiReason()).difficulty(session.getUserDifficulty()).build();

                    MyReport savedReport = myReportRepository.save(newReport);

                    if (hasUserMessage) {
                        if (aiInsight.getCorrections() != null) {
                            saveCorrections(savedReport, aiInsight.getCorrections());
                        }
                        if (aiInsight.getToneAnalysis() != null) {
                            saveConversationTone(savedReport, aiInsight.getToneAnalysis());
                        }
                    }
                    return savedReport;
                });

        return buildReportDetailDTO(myReport, session, messages);
    }

    private void saveConversationTone(MyReport report, MyReportResponseDTO.ToneAnalysisDTO toneDTO) {
        if (toneDTO == null) return;
        ConversationTone tone = ConversationTone.builder()
                .report(report).userTone(toneDTO.getUserTone()).expectedTone(toneDTO.getExpectedTone()).build();
        toneRepository.save(tone);
        report.addConversationTone(tone); // 메모리 동기화
    }

    private void saveCorrections(MyReport report, List<MyReportResponseDTO.CorrectionDTO> correctionDTOs) {
        List<ConversationCorrection> corrections = correctionDTOs.stream()
                .map(dto -> {
                    ConversationCorrection correction = ConversationCorrection.builder()
                            .report(report).originalContent(dto.getOriginal())
                            .correctedContent(dto.getCorrected()).correctionReason(dto.getReason()).build();
                    report.getCorrections().add(correction); // 메모리 동기화
                    return correction;
                }).collect(Collectors.toList());
        correctionRepository.saveAll(corrections);
    }

    private MyReportResponseDTO.AiInsightCardDTO getAiInsight(List<ConversationMessage> messages, MyRole myRole) {
        String transcript = messages.stream()
                .map(m -> String.format("[%s]: %s", m.getSenderRole(), m.getContent()))
                .collect(Collectors.joining("\n"));
        String aiJsonResponse = aiAnalysisService.getAnalysisResult(transcript, myRole);
        try {
            MyReportResponseDTO.AiInsightCardDTO result = objectMapper.readValue(extractJson(aiJsonResponse), MyReportResponseDTO.AiInsightCardDTO.class);
            if (result.getCorrections() != null) {
                List<String> userContents = messages.stream().filter(m -> SenderRole.USER.equals(m.getSenderRole()))
                        .map(m -> m.getContent().trim()).collect(Collectors.toList());
                result.getCorrections().removeIf(c -> userContents.stream().noneMatch(content -> content.equals(c.getOriginal().trim())));
            }
            return result;
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    private String extractJson(String raw) {
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");
        return raw.substring(start, end + 1);
    }

    private MyReportResponseDTO.AiInsightCardDTO getAiInsightDTO(MyReport myReport) {
        List<MyReportResponseDTO.CorrectionDTO> correctionDTOs = myReport.getCorrections().stream()
                .map(c -> MyReportResponseDTO.CorrectionDTO.builder().original(c.getOriginalContent())
                        .corrected(c.getCorrectedContent()).reason(c.getCorrectionReason()).build())
                .collect(Collectors.toList());

        MyReportResponseDTO.ToneAnalysisDTO toneDTO = null;
        if (myReport.getConversationTone() != null) {
            toneDTO = MyReportResponseDTO.ToneAnalysisDTO.builder()
                    .userTone(myReport.getConversationTone().getUserTone())
                    .expectedTone(myReport.getConversationTone().getExpectedTone()).build();
        }

        return MyReportResponseDTO.AiInsightCardDTO.builder()
                .aiSummary(myReport.getAiSummary()).aiReason(myReport.getAiReason())
                .toneAnalysis(toneDTO).corrections(correctionDTOs).build();
    }

    private MyReportResponseDTO.SessionSummaryDTO buildSessionSummary(ConversationSession session, MyReport myReport) {
        LocalTime totalTime = (session.getTotalTime() != null) ? LocalTime.ofSecondOfDay(session.getTotalTime()) : LocalTime.MIDNIGHT;
        return MyReportResponseDTO.SessionSummaryDTO.builder()
                .avatarName(session.getMyRole().getAvatar().getName()).avatarImgUrl(session.getMyRole().getAvatar().getImgUrl())
                .job(session.getMyRole().getJob().getDescription()).situation(session.getMyRole().getSituation().getDescription())
                .totalTime(totalTime).sentenceCount(session.getSentenceCount())
                .difficulty(myReport != null ? myReport.getDifficulty() : session.getUserDifficulty())
                .createdAt(session.getStartedAt().toLocalDate()).build();
    }

    private List<MyReportResponseDTO.MessageLogDTO> buildMessageLogs(List<ConversationMessage> messages) {
        return messages.stream().map(m -> MyReportResponseDTO.MessageLogDTO.builder()
                .messageId(m.getId()).senderRole(m.getSenderRole()).content(m.getContent())
                .audioUrl(m.getAudioUrl()).createdAt(m.getCreatedAt()).build()).collect(Collectors.toList());
    }

    private MyReportResponseDTO.ReportDetailDTO buildReportDetailDTO(MyReport myReport, ConversationSession session, List<ConversationMessage> messages) {
        User user = session.getMyRole().getUser();
        return MyReportResponseDTO.ReportDetailDTO.builder()
                .reportId(myReport.getId()).sessionSummary(buildSessionSummary(session, myReport))
                .aiInsightCard(getAiInsightDTO(myReport)).userReflection(myReport.getUserReflection())
                .conversationLog(buildMessageLogs(messages)).isLogLocked(false)
                .usedLogViewCount(user.getTotalLogViewCount()).maxLogViewCount(MAX_FREE_VIEW_COUNT).build();
    }

    private void validateReportOwner(MyReport report, User user) {
        User reportOwner = report.getSession().getMyRole().getUser();
        if (!reportOwner.getId().equals(user.getId())) {
            throw new MyReportException(MyReportErrorCode.REPORT_ACCESS_DENIED);
        }
    }

    @Transactional
    public MyReportResponseDTO.DeleteReportResultDTO deleteReport(Long reportId, Long userId) {
        User user = findUser(userId);
        MyReport report = myReportRepository.findById(reportId).orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));
        validateReportOwner(report, user);
        if (report.getSession() != null) report.getSession().unlinkReport();
        myReportRepository.delete(report);
        return MyReportConverter.toDeleteReportResultDTO(reportId);
    }

    private List<ConversationMessage> generateDummyMessages(MyReport report) {
        ConversationSession session = report.getSession();
        return List.of(
                ConversationMessage.builder().session(session).senderRole(SenderRole.AI)
                        .content("Hello. Could you briefly introduce yourself and tell me about your background?")
                        .messageType(MessageType.MAIN).build(),
                ConversationMessage.builder().session(session).senderRole(SenderRole.USER)
                        .content("Sure. I have over 5 years of experience in digital marketing. " +
                                "I started my career at a startup where I managed social media campaigns.")
                        .messageType(MessageType.MAIN).build(),
                ConversationMessage.builder().session(session).senderRole(SenderRole.AI)
                        .content("That sounds impressive. Can you describe a specific challenge you faced?")
                        .messageType(MessageType.FOLLOW).build(),
                ConversationMessage.builder().session(session).senderRole(SenderRole.USER)
                        .content("One major challenge was a sudden drop in engagement due to algorithm changes. " +
                                "I pivoted our strategy to focus more on short-form video content.")
                        .messageType(MessageType.FOLLOW).build(),
                ConversationMessage.builder().session(session).senderRole(SenderRole.AI)
                        .content("I see. How do you usually prioritize tasks when you have multiple deadlines?")
                        .messageType(MessageType.MAIN).build(),
                ConversationMessage.builder().session(session).senderRole(SenderRole.USER)
                        .content("I use a priority matrix to categorize tasks by urgency and importance.")
                        .messageType(MessageType.MAIN).build()
        );
    }
}