package com.example.speakOn.domain.mySpeak.repository;

import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConversationMessageRepository {

    private final EntityManager em;

    public void save(ConversationMessage message) {
        em.persist(message);
    }

    public ConversationMessage findById(Long messageId) {
        return em.find(ConversationMessage.class, messageId);
    }

    public List<ConversationMessage> findBySessionIdAndSenderRole(Long sessionId, SenderRole senderRole) {
        return em.createQuery("select m from ConversationMessage m " +
                        "join fetch m.session " +
                        "where m.session.id = :sessionId and m.senderRole = :senderRole " +
                        "ORDER BY m.createdAt ASC")
                .setParameter("sessionId", sessionId)
                .setParameter("senderRole", senderRole)
                .getResultList();
    }
}
