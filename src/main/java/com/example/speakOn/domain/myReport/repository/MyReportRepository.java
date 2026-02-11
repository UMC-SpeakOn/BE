package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MyReportRepository extends JpaRepository<MyReport, Long>, MyReportRepositoryCustom {
    Optional<MyReport> findBySession(ConversationSession session);
    boolean existsBySessionId(Long sessionId);
}