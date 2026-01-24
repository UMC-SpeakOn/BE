package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyReportRepository {

    private final EntityManager em;

    public MyReport save(MyReport report) {
        em.persist(report);
        return report;
    }

    public Optional<MyReport> findById(Long reportId) {
        return Optional.ofNullable(em.find(MyReport.class, reportId));
    }

    /**
     * 사용자의 리포트 목록 최신순으로 조회 (직무/상황 필터링 포함)
     */
    public List<MyReport> findAllByUserAndFilters(User user, JobType job, SituationType situation) {
        return em.createQuery(
                        "SELECT r FROM MyReport r " +
                                "JOIN r.session s " +
                                "JOIN s.myRole m " +
                                "WHERE m.user = :user " +
                                "AND (:job IS NULL OR m.job = :job) " +
                                "AND (:situation IS NULL OR m.situation = :situation) " +
                                "ORDER BY r.createdAt DESC", MyReport.class)
                .setParameter("user", user)
                .setParameter("job", job)
                .setParameter("situation", situation)
                .getResultList();
    }
}