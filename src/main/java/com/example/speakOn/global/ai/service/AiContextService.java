package com.example.speakOn.global.ai.service;

import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.global.ai.entity.AiConversationContext;
import com.example.speakOn.global.ai.repository.AiConversationContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiContextService {

    private final AiConversationContextRepository aiContextRepository;

    /**
     * 문맥 조회 혹은 생성 (트랜잭션 보장)
     */
    @Transactional
    public AiConversationContext getOrCreateContext(ConversationSession session) {
        return aiContextRepository.findBySessionId(session.getId())
                .orElseGet(() -> {
                    try {
                        return aiContextRepository.save(
                                AiConversationContext.builder().session(session).depth(0).build()
                        );
                    } catch (Exception e) {
                        return aiContextRepository.findBySessionId(session.getId())
                                .orElseThrow(() -> new IllegalStateException("Context create failed"));
                    }
                });
    }

    /**
     * 문맥 업데이트 (Dirty Checking 작동 보장)
     */
    @Transactional
    public void updateContext(Long contextId, int newDepth, String newMessage) {
        AiConversationContext context = aiContextRepository.findById(contextId)
                .orElseThrow(() -> new IllegalStateException("Context not found for update"));

        context.updateContext(newDepth, newMessage);
    }
}