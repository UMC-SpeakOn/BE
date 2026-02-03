package com.example.speakOn.global.ai.repository;

import com.example.speakOn.global.ai.entity.AiConversationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiConversationContextRepository extends JpaRepository<AiConversationContext, Long> {

    /**
     * 세션 ID를 기반으로 AI 대화 컨텍스트(Depth, 문맥)를 조회합니다.
     * * @param sessionId ConversationSession의 PK
     * @return AiConversationContext (존재하지 않을 경우 Empty)
     */
    Optional<AiConversationContext> findBySessionId(Long sessionId);
}