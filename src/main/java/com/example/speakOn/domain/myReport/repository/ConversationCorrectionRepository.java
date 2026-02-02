package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.ConversationCorrection;
import com.example.speakOn.domain.myReport.entity.MyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationCorrectionRepository extends JpaRepository<ConversationCorrection, Long> {
    /**
     * 특정 리포트에 속한 모든 교정 내역을 조회합니다.
     */
    List<ConversationCorrection> findAllByReport(MyReport report);

    /**
     * 리포트 ID를 기반으로 모든 교정 내역을 조회합니다.
     */
    List<ConversationCorrection> findAllByReportId(Long reportId);
}
