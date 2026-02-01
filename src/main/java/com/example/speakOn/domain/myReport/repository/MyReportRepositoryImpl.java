package com.example.speakOn.domain.myReport.repository;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.domain.myReport.dto.request.MyReportRequest;
import com.example.speakOn.domain.myReport.entity.MyReport;
import com.example.speakOn.domain.myReport.entity.QMyReport;
import com.example.speakOn.domain.myReport.entity.QConversationTone;
import com.example.speakOn.domain.mySpeak.entity.QConversationSession;
import com.example.speakOn.domain.myRole.entity.QMyRole;
import com.example.speakOn.domain.myRole.enums.JobType;
import com.example.speakOn.domain.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MyReportRepositoryImpl implements MyReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MyReport> findAllByUserAndFilters(User user, MyReportRequest.ReportFilterDTO filter, Pageable pageable) {
        MyReportRequest.ReportFilterDTO safeFilter =
                (filter != null) ? filter : new MyReportRequest.ReportFilterDTO();
        QMyReport report = QMyReport.myReport;
        QConversationSession session = QConversationSession.conversationSession;
        QMyRole myRole = QMyRole.myRole;

        List<MyReport> content = queryFactory
                .selectFrom(report)
                .join(report.session, session).fetchJoin()
                .join(session.myRole, myRole).fetchJoin()
                .where(
                        myRole.user.eq(user),
                        dateBetween(filter.getStartDate(), filter.getEndDate()),
                        jobEq(filter.getJob()),
                        situationEq(filter.getSituation())
                )
                .orderBy(report.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Optional<MyReport> findReportWithAllDetails(Long reportId) {
        QMyReport report = QMyReport.myReport;
        QConversationSession session = QConversationSession.conversationSession;
        QMyRole myRole = QMyRole.myRole;
        QConversationTone tone = QConversationTone.conversationTone;

        return Optional.ofNullable(queryFactory
                .selectFrom(report)
                .join(report.session, session).fetchJoin()
                .join(session.myRole, myRole).fetchJoin()
                .leftJoin(report.conversationTone, tone).fetchJoin()
                .where(report.id.eq(reportId))
                .fetchOne());
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return null;
        return QMyReport.myReport.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    private BooleanExpression jobEq(JobType job) {
        return job != null ? QMyRole.myRole.job.eq(job) : null;
    }

    private BooleanExpression situationEq(SituationType situation) {
        return situation != null ? QMyRole.myRole.situation.eq(situation) : null;
    }
}