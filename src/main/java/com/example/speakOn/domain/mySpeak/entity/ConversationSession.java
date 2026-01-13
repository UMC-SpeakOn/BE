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

    @Column(name = "target_question_count", nullable = false)
    private Integer targetQuestionCount;

    // 현재까지 진행된 메인 질문 개수 (꼬리 질문은 카운트 제외)
    @Column(name = "current_question_count", nullable = false)
    @Builder.Default
    private Integer currentQuestionCount = 0;

    @Column(name = "sentence_count")
    @Builder.Default
    private Integer sentenceCount = 0;

    // 총 소요 시간 (초 단위 저장)
    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "user_difficulty")
    private Integer userDifficulty;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

     // 메인 질문을 던질 때마다 카운트를 증가
    public void incrementQuestionCount() {
        this.currentQuestionCount++;
    }

    // 대화 종료 시 결과 데이터 업데이트
    public void completeSession(Integer totalTime, Integer sentenceCount, Integer difficulty) {
        this.status = SessionStatus.COMPLETED;
        this.totalTime = totalTime;
        this.sentenceCount = sentenceCount;
        this.userDifficulty = difficulty;
        this.endedAt = LocalDateTime.now();
    }
}