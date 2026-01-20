package com.example.speakOn.domain.mySpeak.repository;

import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
