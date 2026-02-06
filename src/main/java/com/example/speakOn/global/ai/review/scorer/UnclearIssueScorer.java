package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class UnclearIssueScorer implements IssueScorer {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern CLEAN_TEXT = Pattern.compile("[^a-z0-9'\\s]");

    // 모호한 표현
    private static final List<Pattern> VAGUE_PATTERNS = List.of(
            Pattern.compile("\\bi don't know\\b"), Pattern.compile("\\bnot sure\\b"),
            Pattern.compile("\\bmaybe\\b"), Pattern.compile("\\bit depends\\b"),
            Pattern.compile("\\bwhatever\\b"), Pattern.compile("\\bu+m+\\b"), Pattern.compile("\\bu+h+\\b")
    );

    @Override
    public IssueScore score(ChatContext context, SituationType situation) {
        String original = context.originalText();

        // 1. 빈 응답 체크
        if (original == null || original.isBlank()) {
            return new IssueScore(FailureType.UNCLEAR, 1.0, "blank AI response");
        }

        String ai = original.trim();

        // 2. 문자 비율 체크
        double ratio = calculateAlphaRatio(ai);
        if (ratio < 0.55) {
            return new IssueScore(FailureType.UNCLEAR, 0.95, String.format("low alpha ratio: %.2f", ratio));
        }

        // 3. 단어 수 체크
        String norm = normalize(ai);
        int wc = wordCount(norm);

        if (wc <= 3) return new IssueScore(FailureType.UNCLEAR, 0.85, "too short (<=3)");
        if (wc <= 6) return new IssueScore(FailureType.UNCLEAR, 0.65, "very short (<=6)");

        // 4. 모호한 표현 체크
        if (VAGUE_PATTERNS.stream().anyMatch(p -> p.matcher(norm).find())) {
            return new IssueScore(FailureType.UNCLEAR, 0.75, "vague phrasing");
        }

        return new IssueScore(FailureType.NONE, 0.0, "ok");
    }

    private double calculateAlphaRatio(String s) {
        int alpha = 0, total = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) continue;
            total++;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) alpha++;
        }
        return total == 0 ? 0.0 : (double) alpha / total;
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