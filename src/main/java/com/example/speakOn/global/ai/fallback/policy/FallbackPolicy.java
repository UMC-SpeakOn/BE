package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.review.model.ReviewState;
import org.springframework.ai.chat.model.ChatResponse;

public interface FallbackPolicy {
    boolean supports(ReviewState state);
    String apply(ChatContext context, ReviewState state);
}
