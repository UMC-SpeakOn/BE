package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class ToneIssueScorer implements IssueScorer {


    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern AGGRESSIVE_PATTERN = Pattern.compile(".*[A-Z]{6,}.*|.*!!!.*");


    private static final List<String> RUDE_CUES = List.of(
            "shut up", "whatever", "that's stupid", "i don't care"
    );


    private static final List<Pattern> CASUAL_PATTERNS = List.of(
            Pattern.compile("\\blol\\b"), Pattern.compile("\\bbro\\b"), Pattern.compile("\\bdude\\b"),
            Pattern.compile("\\bgonna\\b"), Pattern.compile("\\bwanna\\b"), Pattern.compile("\\bkinda\\b"),
            Pattern.compile("\\bya\\b"), Pattern.compile("\\bpls\\b"), Pattern.compile("\\bthx\\b"),
            Pattern.compile("\\bu\\b"), Pattern.compile("\\bur\\b")
    );

    @Override
    public IssueScore score(ChatContext context, SituationType situation) {
        String original = context.originalText();
        if (original == null || original.isBlank()) {
            return new IssueScore(FailureType.NONE, 0.0, "ok");
        }

        String norm = normalize(original);

        // 1. 무례한 표현 체크
        if (RUDE_CUES.stream().anyMatch(norm::contains)) {
            return new IssueScore(FailureType.TONE_ISSUE, 0.90, "rude phrase detected");
        }

        // 2. 공격적 강조
        if (AGGRESSIVE_PATTERN.matcher(original).matches()) {
            return new IssueScore(FailureType.TONE_ISSUE, 0.65, "aggressive emphasis");
        }

        // 3. 캐주얼 표현 횟수 체크
        long casualHits = CASUAL_PATTERNS.stream()
                .filter(p -> p.matcher(norm).find())
                .count();

        if (casualHits >= 3) {
            return new IssueScore(FailureType.TONE_ISSUE, 0.75, "too casual: hits=" + casualHits);
        } else if (casualHits >= 1) {

            double score = (casualHits == 2) ? 0.62 : 0.45;
            return new IssueScore(FailureType.TONE_ISSUE, score, "casual expression detected");
        }

        return new IssueScore(FailureType.NONE, 0.0, "ok");
    }

    private String normalize(String s) {
        return WHITESPACE.matcher(s.trim().toLowerCase()).replaceAll(" ");
    }
}