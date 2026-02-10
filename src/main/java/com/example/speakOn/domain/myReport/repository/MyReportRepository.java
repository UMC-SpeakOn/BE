package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MyReportRepository extends JpaRepository<MyReport, Long>, MyReportRepositoryCustom {
    MyReport findBySession(ConversationSession session);
    boolean existsBySessionId(Long sessionId);

    /**
     * 사용자의 모든 리포트 벌크 삭제 (회원 탈퇴 시)
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MyReport mr WHERE mr.session.myRole.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}