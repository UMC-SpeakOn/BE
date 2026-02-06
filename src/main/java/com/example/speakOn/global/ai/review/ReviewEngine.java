package com.example.speakOn.global.ai.review;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.scorer.IssueScore;
import com.example.speakOn.global.ai.review.scorer.IssueScorer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewEngine {

    private final List<IssueScorer> scorers;


    public ReviewState review(ChatContext context, SituationType situation) {
        if (context == null) {
            return new ReviewState(FailureType.UNCLEAR, 0.90, "null ChatContext");
        }

        IssueScore top = scorers.stream()
                .map(s -> s.score(context, situation))
                .max(Comparator.comparingDouble(IssueScore::score))
                .orElse(new IssueScore(FailureType.NONE, 0.0, "no scorers"));

        //시나리오 별 민감도
        double threshold = switch (situation) {
            case INTERVIEW -> 0.60;
            case MEETING -> 0.55;
            case ONE_ON_ONE_MEETING -> 0.55;

        };

        if (top.score() < threshold) {
            return new ReviewState(FailureType.NONE, 1.0, "ok (maxScore=" + top.score() + ")");
        }

        return new ReviewState(top.type(), clamp01(top.score()), top.reason());
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}