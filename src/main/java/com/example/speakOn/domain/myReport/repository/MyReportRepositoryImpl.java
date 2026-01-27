package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyReportRepositoryImpl implements MyReportRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<MyReport> findAllByUserAndFilters(User user, JobType job, SituationType situation) {
        String jpql = "SELECT r FROM MyReport r " +
                "JOIN FETCH r.session s " +
                "JOIN FETCH s.myRole m " +
                "WHERE m.user = :user ";

        if (job != null) {
            jpql += "AND m.job = :job ";
        }
        if (situation != null) {
            jpql += "AND m.situation = :situation ";
        }

        jpql += "ORDER BY r.createdAt DESC";

        var query = em.createQuery(jpql, MyReport.class)
                .setParameter("user", user);

        if (job != null) query.setParameter("job", job);
        if (situation != null) query.setParameter("situation", situation);

        return query.getResultList();
    }
}