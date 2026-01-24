package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.dto.ChatRequest;

public record ChatContext(
        ChatRequest request,
        String originalOutput
) {
    public static ChatContext of(ChatRequest request, String output) {
        return new ChatContext(request, output);
    }
}