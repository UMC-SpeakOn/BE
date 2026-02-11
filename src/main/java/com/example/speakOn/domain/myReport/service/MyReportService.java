package com.example.speakOn.domain.myReport.service;

import com.example.speakOn.domain.myReport.entity.ReportViewHistory;
import com.example.speakOn.domain.myReport.service.ReportViewHistoryService;
import com.example.speakOn.domain.myReport.repository.ReportViewHistoryRepository;
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
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.mySpeak.repository.ConversationSessionRepository;
import com.example.speakOn.domain.subscription.repository.SubscriptionRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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
    private final AiSpeakService aiSpeakService;
    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper;
    private final ConversationCorrectionRepository correctionRepository;
    private final MyRoleRepository myRoleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReportViewHistoryService reportViewHistoryService;
    private final ReportViewHistoryRepository reportViewHistoryRepository;
    private final int MAX_FREE_VIEW_COUNT = 5;

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    /**
     * 리포트 목록 조회
     */
    public MyReportResponseDTO.ReportSummaryListDTO getReportList(Long userId, MyReportRequest.ReportFilterDTO filter, Pageable pageable) {
        User user = findUser(userId);

        Slice<MyReport> reports = myReportRepository.findAllByUserAndFilters(user, filter, pageable);

        return MyReportConverter.toReportSummaryListDTOFromSlice(reports);
    }

    /**
     * 리포트 상세 조회
     *
     * @param reportId 조회할 리포트의 ID
     * @param userId 현재 로그인한 유저의 ID
     * @param viewUUID 중복 차감 방지용 식별자 (새로고침 시 유지, 재진입 시 갱신)
     * @return 리포트 상세 정보 DTO
     */
    @Transactional
    public MyReportResponseDTO.ReportDetailDTO getReportDetail(Long reportId, Long userId, String viewUUID) {
        User user = findUser(userId);

        MyReport report = myReportRepository.findReportWithAllDetails(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);

        // 구독 여부 확인
        boolean isSubscribed = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .isPresent();

        boolean isLogLocked = true;

        boolean alreadyPaid = reportViewHistoryRepository
                .existsByReportAndUserAndViewUUID(report, user, viewUUID);

        if (isSubscribed || alreadyPaid) {
            isLogLocked = false;
        } else if (user.getTotalLogViewCount() < MAX_FREE_VIEW_COUNT) {
            try {
                ReportViewHistory history = ReportViewHistory.builder()
                        .report(report)
                        .user(user)
                        .viewUUID(viewUUID)
                        .build();

                reportViewHistoryService.trySaveHistory(history);

                user.incrementLogViewCount();
                isLogLocked = false;

            } catch (DataIntegrityViolationException e) {
                log.warn("Concurrent report view detected for UUID: {}", viewUUID);
                isLogLocked = false;
            }
        }

        // (잠금 해제된 경우) 로그 데이터 조회
        List<ConversationMessage> messages = List.of();
        if (!isLogLocked) {
            ConversationSession session = report.getSession();
            if (session != null) {
                messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(session);
            }
        }

        return MyReportConverter.toReportDetailDTO(
                report,
                messages,
                isLogLocked,
                user.getTotalLogViewCount()
        );
    }

    /**
     * 대화 로그 상세 조회
     */
    @Transactional
    public MyReportResponseDTO.MessageLogListDTO getConversationLogs(Long reportId, Long userId, String viewUUID) {
        User user = findUser(userId);
        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);

        boolean isSubscribed = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .isPresent();

        boolean isLogLocked = true;

        // 새로고침 체크
        boolean isRefreshedRequest = reportViewHistoryRepository
                .existsByReportAndUserAndViewUUID(report, user, viewUUID);

        if (isSubscribed || isRefreshedRequest) {
            isLogLocked = false;
        } else if (user.getTotalLogViewCount() < MAX_FREE_VIEW_COUNT) {
            try {
                ReportViewHistory history = ReportViewHistory.builder()
                        .report(report)
                        .user(user)
                        .viewUUID(viewUUID)
                        .build();
                reportViewHistoryService.trySaveHistory(history);

                user.incrementLogViewCount();
                isLogLocked = false;
            } catch (DataIntegrityViolationException e) {
                log.warn("Concurrent conversation log view detected for UUID: {}", viewUUID);
                isLogLocked = false;
            }
        }

        List<ConversationMessage> messages = List.of();
        if (!isLogLocked) {
            ConversationSession session = report.getSession();
            messages = (session != null)
                    ? messageRepository.findAllBySessionOrderByCreatedAtAsc(session)
                    : List.of();
        }

        return MyReportConverter.toMessageLogListDTO(
                reportId,
                messages,
                isLogLocked,
                user.getTotalLogViewCount()
        );
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
        ConversationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.SESSION_NOT_FOUND));

        List<ConversationMessage> messages = messageRepository.findAllBySessionOrderByCreatedAtAsc(session);
        boolean hasUserMessage = messages.stream()
                .anyMatch(m -> SenderRole.USER.equals(m.getSenderRole()));


        MyReport myReport = myReportRepository.findBySession(session)
                .orElseGet(() -> {
                    MyReportResponseDTO.AiInsightCardDTO aiInsight;

                    if (hasUserMessage) {
                        aiInsight = getAiInsight(messages, session.getMyRole());
                    } else {
                        aiInsight = MyReportResponseDTO.AiInsightCardDTO.builder()
                                .aiSummary("진행된 대화가 없어 AI 분석이 수행되지 않았습니다.")
                                .aiReason(List.of("사용자 답변이 감지되지 않았습니다. 아바타와 대화를 나눠보세요!"))
                                .corrections(Collections.emptyList())
                                .build();
                    }

                    MyReport newReport = MyReport.builder()
                            .session(session)
                            .aiSummary(aiInsight.getAiSummary())
                            .aiReason(aiInsight.getAiReason())
                            .difficulty(session.getUserDifficulty())
                            .build();

                    MyReport savedReport = myReportRepository.save(newReport);

                    if (hasUserMessage && aiInsight.getCorrections() != null) {
                        saveCorrections(savedReport, aiInsight.getCorrections());
                    }

                    return savedReport;
                });

        return buildReportDetailDTO(myReport, session, messages);
    }
    private void saveCorrections(MyReport report, List<MyReportResponseDTO.CorrectionDTO> correctionDTOs) {
        List<ConversationCorrection> corrections = correctionDTOs.stream()
                .map(dto -> ConversationCorrection.builder()
                        .report(report)
                        .originalContent(dto.getOriginal())
                        .correctedContent(dto.getCorrected())
                        .correctionReason(dto.getReason())
                        .build())
                .collect(Collectors.toList());
        correctionRepository.saveAll(corrections);
    }
    /**
     * AI 분석 카드 생성 로직
     */
    private MyReportResponseDTO.AiInsightCardDTO getAiInsight(List<ConversationMessage> messages, MyRole myRole) {
        String transcript = messages.stream()
                .map(m -> String.format("[%s]: %s", m.getSenderRole(), m.getContent()))
                .collect(Collectors.joining("\n"));

        String aiJsonResponse = aiAnalysisService.getAnalysisResult(transcript, myRole);

        try {
            String cleaned = extractJson(aiJsonResponse);
            MyReportResponseDTO.AiInsightCardDTO result = objectMapper.readValue(cleaned, MyReportResponseDTO.AiInsightCardDTO.class);

            if (result.getCorrections() != null) {
                List<String> userContents = messages.stream()
                        .filter(m -> "USER".equals(m.getSenderRole().name()))
                        .map(m -> m.getContent().trim())
                        .collect(Collectors.toList());

                // 교정 제안 중 원문이 사용자 대화 목록에 없는 경우 삭제
                result.getCorrections().removeIf(c -> {
                            String originalTrimmed = c.getOriginal().trim();
                            return userContents.stream().noneMatch(content -> content.equals(originalTrimmed));
                        }
                );
            }
            return result;
        } catch (JsonProcessingException e) {
            log.error("AI JSON Parsing Failed. Raw: {}", aiJsonResponse);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
        }

    private String extractJson(String raw) {
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");
        if (start == -1 || end == -1) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
        return raw.substring(start, end + 1);
    }

    /**
     * DB에 저장된 리포트 엔티티를 바탕으로 AI 인사이트 DTO를 생성합니다.
     */
    private MyReportResponseDTO.AiInsightCardDTO getAiInsightDTO(MyReport myReport) {
        // 1. 리포트에 저장된 교정 리스트를 DTO 리스트로 변환
        List<MyReportResponseDTO.CorrectionDTO> correctionDTOs = myReport.getCorrections().stream()
                .map(c -> MyReportResponseDTO.CorrectionDTO.builder()
                        .original(c.getOriginalContent())
                        .corrected(c.getCorrectedContent())
                        .reason(c.getCorrectionReason())
                        .build())
                .collect(Collectors.toList());

        // 2. 최종 카드 DTO 조립
        return MyReportResponseDTO.AiInsightCardDTO.builder()
                .aiSummary(myReport.getAiSummary())
                .aiReason(myReport.getAiReason())
                .corrections(correctionDTOs)
                .build();
    }


    /**
     * 세션 요약 정보 조립 (중복 빌더 제거 및 MyRole 참조 최적화)
     */
    private MyReportResponseDTO.SessionSummaryDTO buildSessionSummary(ConversationSession session, MyReport myReport) {
        LocalTime totalTime = (session.getTotalTime() != null)
                ? LocalTime.ofSecondOfDay(session.getTotalTime())
                : LocalTime.MIDNIGHT;

        MyRole myRole = session.getMyRole();

        return MyReportResponseDTO.SessionSummaryDTO.builder()
                .avatarName(myRole.getAvatar().getName())
                .avatarImgUrl(myRole.getAvatar().getImgUrl())
                .job(myRole.getJob().getDescription())
                .situation(myRole.getSituation().getDescription())
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

    private MyReportResponseDTO.ReportDetailDTO buildReportDetailDTO(MyReport myReport, ConversationSession session, List<ConversationMessage> messages) {
        User user = session.getMyRole().getUser();

        return MyReportResponseDTO.ReportDetailDTO.builder()
                .reportId(myReport.getId())
                .sessionSummary(buildSessionSummary(session, myReport))
                .aiInsightCard(getAiInsightDTO(myReport))
                .userReflection(myReport.getUserReflection())
                .conversationLog(buildMessageLogs(messages))
                .isLogLocked(false)
                .usedLogViewCount(user.getTotalLogViewCount())
                .maxLogViewCount(MAX_FREE_VIEW_COUNT)
                .build();
    }

    /**
     * 리포트 삭제
     */
    @Transactional
    public MyReportResponseDTO.DeleteReportResultDTO deleteReport(Long reportId, Long userId) {
        User user = findUser(userId);

        MyReport report = myReportRepository.findById(reportId)
                .orElseThrow(() -> new MyReportException(MyReportErrorCode.REPORT_NOT_FOUND));

        validateReportOwner(report, user);
        myReportRepository.delete(report);

        return MyReportConverter.toDeleteReportResultDTO(reportId);
    }
}