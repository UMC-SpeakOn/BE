package com.example.speakOn.global.ai.service;

import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.global.ai.entity.AiConversationContext;
import com.example.speakOn.global.ai.repository.AiConversationContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiContextService {
    private final AiConversationContextRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 생성 즉시 커밋하여 비동기 쓰레드에 노출
    public AiConversationContext getOrCreateContext(ConversationSession session) {
        return repository.findBySessionId(session.getId())
                .orElseGet(() -> {
                    AiConversationContext newContext = AiConversationContext.builder()
                            .session(session)
                            .depth(0)
                            .build();
                    return repository.saveAndFlush(newContext); // Flush까지 확실히
                });
    }

    @Transactional
    public void updateContext(Long contextId, int depth, String aiMessage) {
        AiConversationContext context = repository.findById(contextId)
                .orElseThrow(() -> new RuntimeException("Context not found: ID=" + contextId));

        context.updateContext(depth, aiMessage);
        repository.save(context);
    }
}