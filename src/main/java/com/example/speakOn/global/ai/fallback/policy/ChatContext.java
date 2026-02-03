package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.domain.ChatRequest;

public record ChatContext(
        ChatRequest chatReq,
        String originalText
) {
    public static ChatContext of(ChatRequest chatReq, String originalText) {
        return new ChatContext(chatReq, originalText);
    }
}