package com.example.speakOn.domain.myReport.service;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.code.MyReportErrorCode;
import com.example.speakOn.domain.myReport.converter.MyReportConverter;
import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.dto.response.MyReportResponseDTO;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.exception.MyReportException;
import com.example.speakOn.domain.myReport.repository.MyReportRepository;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.repository.ConversationMessageRepository;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyReportService {

    private final MyReportRepository myReportRepository;
    private final ConversationMessageRepository messageRepository;
    private final UserRepository userRepository;

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

        validateReportOwner(report, user);
        return MyReportConverter.toReportDetailDTO(report);
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
}