package com.example.speakOn.domain.mySpeak.entity;

import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.enums.SessionStatus;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "conversation_session")
public class ConversationSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_role_id", nullable = false)
    private MyRole myRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    /**
     * 와이어프레임의 '세션 목표' (5개: 빠르게 / 10개: 충분히)
     */
    @Column(name = "target_question_count", nullable = false)
    private Integer targetQuestionCount;

    /**
     * 현재까지 진행된 '메인 질문'의 개수 (꼬리 질문은 카운트 제외)
     */
    @Column(name = "current_question_count", nullable = false)
    @Builder.Default
    private Integer currentQuestionCount = 0;

    /**
     * 결과 화면 항목 1: 총 문장 수 (와이어프레임 기준 '000 문장' 표시용)
     */
    @Column(name = "sentence_count")
    @Builder.Default
    private Integer sentenceCount = 0;

    /**
     * 결과 화면 항목 2: 총 소요 시간 (초 단위 저장)
     */
    @Column(name = "total_time")
    private Integer totalTime;

    /**
     * 결과 화면 항목 3: 자가 평가 난이도 (와이어프레임의 별점 5점 만점, Integer 확정)
     */
    @Column(name = "user_difficulty")
    private Integer userDifficulty;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    // --- 비즈니스 로직 (도메인 메서드) ---

    /**
     * AI가 메인 질문을 던질 때마다 카운트를 증가시킵니다.
     */
    public void incrementQuestionCount() {
        this.currentQuestionCount++;
    }

    /**
     * 대화 종료 시 결과 데이터를 업데이트합니다.
     */
    public void completeSession(Integer totalTime, Integer sentenceCount, Integer difficulty) {
        this.status = SessionStatus.COMPLETED;
        this.totalTime = totalTime;
        this.sentenceCount = sentenceCount;
        this.userDifficulty = difficulty;
        this.endedAt = LocalDateTime.now();
    }
}