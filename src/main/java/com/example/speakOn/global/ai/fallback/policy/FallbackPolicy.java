package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.review.model.ReviewState;

public interface FallbackPolicy {
    boolean supports(ReviewState state);
    String apply(ChatContext context, ReviewState state);
}
