package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ToneFallbackPolicy implements FallbackPolicy {

    @Override
    public boolean supports(ReviewState state) {
        return state.failureType() == FailureType.TONE_ISSUE;
    }

    @Override
    public String apply(ChatContext context, ReviewState state) {
        return "Let’s keep it professional. Could you answer again in a more formal tone? "
                + "For example: “Certainly. In my previous role, I …”";
    }
}