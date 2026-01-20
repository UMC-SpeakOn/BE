package com.example.speakOn.domain.mySpeak.repository;


import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConversationSessionRepository {

    private final EntityManager em;

    public ConversationSession save(ConversationSession session) {
        em.persist(session);
        return session;
    }

    public ConversationSession findById(Long sessionId) {
        return em.find(ConversationSession.class, sessionId);
    }
}