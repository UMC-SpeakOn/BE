package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MyReportRepository extends JpaRepository<MyReport, Long> {

    // 사용자의 리포트 목록을 최신순으로 조회 (필터링 포함)
    @Query("SELECT r FROM MyReport r JOIN r.session s JOIN s.myRole m " +
            "WHERE m.user = :user " +
            "AND (:job IS NULL OR m.job = :job) " +
            "AND (:situation IS NULL OR m.situation = :situation) " +
            "ORDER BY r.createdAt DESC")
    List<MyReport> findAllByUserAndFilters(@Param("user") User user,
                                           @Param("job") JobType job,
                                           @Param("situation") SituationType situation);
}