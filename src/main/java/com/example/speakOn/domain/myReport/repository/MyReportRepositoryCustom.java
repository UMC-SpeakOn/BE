package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface MyReportRepositoryCustom {
    Slice<MyReport> findAllByUserAndFilters(User user, MyReportRequest.ReportFilterDTO filter, Pageable pageable);
    Optional<MyReport> findReportWithAllDetails(Long reportId);
}