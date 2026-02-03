package com.example.speakOn.global.ai.entity;

import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "ai_conversation_context")
public class AiConversationContext extends BaseEntity {

    // 세션과 1:1 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private ConversationSession session;

    // 질문 깊이
    @Column(name = "depth", nullable = false)
    @Builder.Default
    private Integer depth = 0;

    // 직전 AI의 답변 (문맥 유지용)
    @Column(name = "previous_ai_message", columnDefinition = "TEXT")
    private String previousAiMessage;

    public void updateContext(Integer depth, String previousAiMessage) {
        if (depth != null) {
            this.depth = depth;
        }
        if (previousAiMessage != null && !previousAiMessage.isBlank()) {
            this.previousAiMessage = previousAiMessage;
        }
    }
}