package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.MyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyReportRepository extends JpaRepository<MyReport, Long>, MyReportRepositoryCustom {
}