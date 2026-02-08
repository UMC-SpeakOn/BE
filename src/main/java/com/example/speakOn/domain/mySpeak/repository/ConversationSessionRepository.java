package com.example.speakOn.domain.mySpeak.repository;


import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConversationSessionRepository {

    private final EntityManager em;

    public void save(ConversationSession session) {
        em.persist(session);
    }

    public ConversationSession findById(Long sessionId) {
        return em.find(ConversationSession.class, sessionId);
    }

    public Optional<ConversationSession> findByIdWithAll(Long sessionId) {
        return em.createQuery(
                        "SELECT session FROM ConversationSession session " +
                                "JOIN FETCH session.myRole role " +
                                "JOIN FETCH role.avatar avatar " +
                                "WHERE session.id = :sessionId", ConversationSession.class)
                .setParameter("sessionId", sessionId)
                .getResultList()
                .stream()
                .findFirst();
    }

}