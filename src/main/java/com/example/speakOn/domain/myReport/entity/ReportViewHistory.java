package com.example.speakOn.domain.myReport.entity;

import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "report_view_history", indexes = {
        @Index(name = "idx_view_history_deduplication", columnList = "report_id, user_id, view_uuid")
})
public class ReportViewHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MyReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 프론트엔드에서 생성한 1회용 조회 식별자
    @Column(name = "view_uuid", nullable = false)
    private String viewUUID;
}