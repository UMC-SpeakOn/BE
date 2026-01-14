package com.example.speakOn.domain.myReport.entity;

import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "conversation_correction")
public class ConversationCorrection extends BaseEntity {

    // 리포트 (N:1, 필수)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private MyReport report;

    // 원문 (User)
    @Column(name = "original_content", columnDefinition = "TEXT", nullable = false)
    private String originalContent;

    // 교정문 (AI)
    @Column(name = "corrected_content", columnDefinition = "TEXT", nullable = false)
    private String correctedContent;

    // 교정 이유
    @Column(name = "correction_reason", columnDefinition = "TEXT")
    private String correctionReason;
}
