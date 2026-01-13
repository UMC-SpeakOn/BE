package com.example.speakOn.domain.mySpeak.entity;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.enums.SenderRole;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "conversation_message")
public class ConversationMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ConversationSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_role", nullable = false)
    private SenderRole senderRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 유료 비즈니스 모델: '다시 듣기' 기능을 위한 S3 오디오 파일 URL
     */
    @Column(name = "audio_url", columnDefinition = "TEXT")
    private String audioUrl;

    /**
     * AI 발화의 경우 MAIN/FOLLOW/CLOSING 중 하나를 가집니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;
}