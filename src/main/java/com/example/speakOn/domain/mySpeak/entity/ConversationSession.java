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

    @Column(name = "current_question_count", nullable = false)
    @Builder.Default
    private Integer currentQuestionCount = 0;

    @Column(name = "sentence_count")
    @Builder.Default
    private Integer sentenceCount = 0;

    @Column(name = "user_difficulty")
    private Integer userDifficulty;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    // 메인 질문 카운트 증가
    public void incrementQuestionCount() {
        this.currentQuestionCount++;
    }

    // 대화 종료 및 결과 업데이트
    public void completeSession(Integer difficulty, Integer totalTime, Integer sentenceCount) {
        this.status = SessionStatus.COMPLETED;
        this.userDifficulty = difficulty;
        this.totalTime = totalTime;
        this.sentenceCount = sentenceCount;
        this.endedAt = LocalDateTime.now();
    }
}