package com.example.speakOn.domain.myReport.entity;

import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "my_report")
public class MyReport extends BaseEntity {

    // 대화 세션 (1:1, 필수)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private ConversationSession session;

    // 대화 톤 (1:1, 양방향)
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ConversationTone conversationTone;

    // 대화 교정 (1:N, 양방향)
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ConversationCorrection> corrections = new ArrayList<>();

    // 사용자 소감
    @Column(name = "user_reflection", length = 120)
    private String userReflection;

    // 난의도
    @Column(name = "difficulty")
    private Integer difficulty;

    // AI 요약
    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    // AI 근거 설명
    @Column(name = "ai_reason", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> aiReason = new ArrayList<>();

    // 비즈니스 로직
    public void updateReflectionAndDifficulty(String userReflection, Integer difficulty) {
        this.userReflection = userReflection;
        this.difficulty = difficulty;
    }
}
