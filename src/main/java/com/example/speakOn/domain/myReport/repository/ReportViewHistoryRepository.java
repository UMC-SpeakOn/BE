package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.entity.ReportViewHistory;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportViewHistoryRepository extends JpaRepository<ReportViewHistory, Long> {
    boolean existsByReportAndUserAndViewUUID(MyReport report, User user, String viewUUID);
}