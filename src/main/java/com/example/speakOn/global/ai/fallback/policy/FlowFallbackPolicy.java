package com.example.speakOn.global.ai.fallback.policy;

import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class FlowFallbackPolicy implements FallbackPolicy {

    @Override
    public boolean supports(ReviewState state) {
        return state.failureType() == FailureType.FLOW_ISSUE;
    }

    @Override
    public String apply(ChatContext context, ReviewState state) {
        if (context.chatReq() != null && context.chatReq().isFirstTurn()) {
            return "Thanks for joining today. To start, could you briefly introduce yourself?";
        }
        return "Let’s stay focused. Could you answer with one key point and one example?";
    }
}
