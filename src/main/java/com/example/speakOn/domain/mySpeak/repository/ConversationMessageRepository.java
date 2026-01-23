package com.example.speakOn.domain.mySpeak.repository;

import com.example.speakOn.domain.mySpeak.entity.ConversationMessage;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    // 세션별 대화 내용을 시간순으로 조회
    List<ConversationMessage> findAllBySessionOrderByCreatedAtAsc(ConversationSession session);
}