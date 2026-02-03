package com.example.speakOn.global.ai.repository;

import com.example.speakOn.global.ai.entity.AiConversationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AiConversationContextRepository extends JpaRepository<AiConversationContext, Long> {

    @Query("SELECT a FROM AiConversationContext a WHERE a.session.id = :sessionId")
    Optional<AiConversationContext> findBySessionId(@Param("sessionId") Long sessionId);

}