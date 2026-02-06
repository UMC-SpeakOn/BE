package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static com.example.speakOn.domain.avatar.enums.SituationType.INTERVIEW;
import static com.example.speakOn.domain.avatar.enums.SituationType.ONE_ON_ONE_MEETING;

@Component
public class FlowIssueScorer implements IssueScorer {


    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern CLEAN_TEXT = Pattern.compile("[^a-z0-9'\\s]");

    private static final List<String> QUESTION_CUES = List.of(
            "could you", "can you", "would you", "tell me", "walk me through",
            "what ", "why ", "how ", "please describe", "let's discuss"
    );

    @Override
    public IssueScore score(ChatContext context, SituationType situation) {
        String original = context.originalText();
        if (original == null || original.isBlank()) {
            return new IssueScore(FailureType.NONE, 0.0, "empty");
        }

        String ai = original.trim();
        String norm = normalize(ai);

        // 1. 질문 여부 체크
        boolean hasQuestion = ai.contains("?") ||
                QUESTION_CUES.stream().anyMatch(norm::contains);

        if (!hasQuestion) {
            int wc = wordCount(norm);

            double base = switch (situation) {
                case ONE_ON_ONE_MEETING -> 0.55;
                case INTERVIEW -> 0.60;
                default -> 0.50;
            };

            if (wc <= 12) base += 0.08;
            return new IssueScore(FailureType.FLOW_ISSUE, Math.min(1.0, base), "missing engagement cue");
        }

        return new IssueScore(FailureType.NONE, 0.0, "ok");
    }

    private String normalize(String s) {
        return WHITESPACE.matcher(s.toLowerCase()).replaceAll(" ");
    }

    private int wordCount(String normalized) {
        String cleaned = CLEAN_TEXT.matcher(normalized).replaceAll(" ");
        if (cleaned.isBlank()) return 0;
        return WHITESPACE.split(cleaned.trim()).length;
    }
}