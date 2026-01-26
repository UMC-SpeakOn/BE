package com.example.speakOn.global.ai.fallback;

import com.example.speakOn.global.ai.exception.AiResponseValidator;
import com.example.speakOn.global.ai.exception.AiValidationResult;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.fallback.policy.FallbackPolicy;
import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class AiFallbackHandler {

    private final List<FallbackPolicy> policies;

    /** Technical fallback: response validation failed, parse error, etc. */
    public String handle(Prompt prompt, ChatResponse response, AiValidationResult validation) {
        return "Sorry—there was a technical issue on my side. Could you please say that again?";
    }

    /** Quality fallback: FLOW/TONE/UNCLEAR based on ReviewState */
    public String handle(ChatContext context, ReviewState state) {
        if (state == null) {
            return "Sorry—I didn’t catch that. Could you say it again?";
        }

        return policies.stream()
                .filter(p -> p.supports(state))
                .findFirst()
                .map(p -> p.apply(context, state))
                .orElseGet(() -> defaultMessage(state.failureType()));
    }

    private String defaultMessage(FailureType type) {
        return switch (type) {
            case FLOW_ISSUE ->
                    "Let’s get back on track. Could you answer in one clear sentence?";
            case TONE_ISSUE ->
                    "Let’s keep it professional. Could you rephrase that in a more business-appropriate tone?";
            case UNCLEAR ->
                    "I didn’t quite understand. Could you rephrase it more clearly?";
            case NONE ->
                    "Great. Let’s continue.";
        };
    }
}