package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ToneIssueScorer implements IssueScorer {

    private static final List<String> RUDE = List.of(
            "shut up", "whatever", "that's stupid", "i don't care"
    );

    private static final List<String> CASUAL = List.of(
            "lol", "bro", "dude", "gonna", "wanna", "kinda", "ya", " pls", " thx", " u ", " ur "
    );

    @Override
    public IssueScore score(ChatContext context, ScenarioType scenario) {
        String ai = safe(context.originalText());
        if (ai.isBlank()) return new IssueScore(FailureType.NONE, 0.0, "ok");

        String norm = normalize(ai);

        if (containsAny(norm, RUDE)) {
            return new IssueScore(FailureType.TONE_ISSUE, 0.90, "rude phrase detected");
        }

        int casualHits = countHits(norm, CASUAL);
        if (casualHits >= 3) return new IssueScore(FailureType.TONE_ISSUE, 0.75, "too casual hits=" + casualHits);
        if (casualHits == 2) return new IssueScore(FailureType.TONE_ISSUE, 0.62, "casual hits=2");

        if (ai.matches(".*[A-Z]{6,}.*") || ai.contains("!!!")) {
            return new IssueScore(FailureType.TONE_ISSUE, 0.65, "aggressive emphasis");
        }

        return new IssueScore(FailureType.NONE, 0.0, "ok");
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String normalize(String s) { return safe(s).toLowerCase().replaceAll("\\s+", " "); }

    private static boolean containsAny(String text, List<String> needles) {
        for (String n : needles) if (text.contains(n)) return true;
        return false;
    }

    private static int countHits(String text, List<String> needles) {
        int hits = 0;
        for (String n : needles) if (text.contains(n)) hits++;
        return hits;
    }
}