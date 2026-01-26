package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class UnclearFallbackPolicy implements FallbackPolicy {

    @Override
    public boolean supports(ReviewState state) {
        return state.failureType() == FailureType.UNCLEAR;
    }

    @Override
    public String apply(ChatContext context, ReviewState state) {
        return "I didn’t quite understand. Could you rephrase your answer in one sentence?";
    }
}
