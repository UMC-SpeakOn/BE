package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.review.ScenarioType;

public record ChatContext(
        ChatRequest chatReq,
        String originalText,
        SituationType situation
) {
    public static ChatContext of(ChatRequest chatReq, String originalText, SituationType situation) {
        return new ChatContext(chatReq, originalText, situation);
    }
}