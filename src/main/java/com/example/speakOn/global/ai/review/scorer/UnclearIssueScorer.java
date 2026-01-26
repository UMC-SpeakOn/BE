package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnclearIssueScorer implements IssueScorer {

    private static final List<String> VAGUE = List.of(
            "i don't know", "not sure", "maybe", "it depends", "whatever", "you know", "um", "uh"
    );

    @Override
    public IssueScore score(ChatContext context, ScenarioType scenario) {
        String ai = safe(context.originalText());
        if (ai.isBlank()) {
            return new IssueScore(FailureType.UNCLEAR, 1.0, "blank AI response");
        }

        double alphaRatio = alphaRatio(ai);
        if (alphaRatio < 0.55) {
            return new IssueScore(FailureType.UNCLEAR, 0.95, "low alphabetic ratio: " + alphaRatio);
        }

        int wc = wordCount(ai);
        if (wc <= 3) return new IssueScore(FailureType.UNCLEAR, 0.85, "too short <=3 words");
        if (wc <= 6) return new IssueScore(FailureType.UNCLEAR, 0.65, "very short <=6 words");

        String norm = normalize(ai);
        if (containsAny(norm, VAGUE)) {
            return new IssueScore(FailureType.UNCLEAR, 0.75, "vague phrasing");
        }

        return new IssueScore(FailureType.NONE, 0.0, "ok");
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String normalize(String s) { return safe(s).toLowerCase().replaceAll("\\s+", " "); }

    private static boolean containsAny(String text, List<String> needles) {
        for (String n : needles) if (text.contains(n)) return true;
        return false;
    }

    private static int wordCount(String s) {
        String t = normalize(s).replaceAll("[^a-z0-9'\\s]", " ");
        if (t.isBlank()) return 0;
        return t.trim().split("\\s+").length;
    }

    private static double alphaRatio(String s) {
        int alpha = 0, total = 0;
        for (char c : s.toCharArray()) {
            if (!Character.isWhitespace(c)) total++;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) alpha++;
        }
        return total == 0 ? 0.0 : (double) alpha / total;
    }
}
