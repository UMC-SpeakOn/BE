package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.exception.AiValidationResult;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import com.nimbusds.openid.connect.sdk.Prompt;
import org.springframework.ai.chat.model.ChatResponse;

public record ChatContext(
        ChatRequest chatReq,
        String originalText
) {
    public static ChatContext of(ChatRequest chatReq, String originalText) {
        return new ChatContext(chatReq, originalText);
    }
}