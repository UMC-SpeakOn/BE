package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.example.speakOn.global.ai.review.ScenarioType.INTERVIEW;
import static com.example.speakOn.global.ai.review.ScenarioType.ONE_ON_ONE_MEETING;

@Component
@Order(1)
public class UnclearFallbackPolicy implements FallbackPolicy {

    @Override
    public boolean supports(ReviewState state) {
        return state.failureType() == FailureType.UNCLEAR;
    }

    @Override
    public String apply(ChatContext context, ReviewState state) {
        return switch (context.situation()) {
            case INTERVIEW -> "I missed your point. Could you explain that part of your experience again more clearly?";
            case ONE_ON_ONE_MEETING ->
                    "Sorry, I didn't quite catch that. Could you rephrase your last point for the meeting?";
            default -> "I didn't quite understand. Could you rephrase your answer in one clear sentence?";
        };
    }
}
