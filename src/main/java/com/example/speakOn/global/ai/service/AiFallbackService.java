package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.fallback.policy.FallbackPolicy;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import com.example.speakOn.global.ai.review.scorer.IssueScore;
import com.example.speakOn.global.ai.review.scorer.IssueScorer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFallbackService {

    private final List<IssueScorer> scorers;
    private final List<FallbackPolicy> policies;

    public String reviewAndCorrect(ChatContext context) {
        IssueScore maxScore = new IssueScore(FailureType.NONE, 0.0, "ok");

        // 1. [검토]
        for (IssueScorer scorer : scorers) {
            // 이제 Scorer 내부에서도 context.situation()을 사용합니다.
            IssueScore score = scorer.score(context, context.situation());
            if (score.score() > maxScore.score()) {
                maxScore = score;
            }
            if (maxScore.score() >= 0.9) break; // 조기 종료 (속도 최적화)
        }

        final IssueScore finalMax = maxScore;

        // 2. [판단] 임계값 미만이면 원본 반환
        if (finalMax.score() < 0.6) {
            return context.originalText();
        }

        // 3. [조치] Policy 매칭
        log.warn("[AI Fallback] Issue Detected: type={}, score={}, reason={}",
                finalMax.type(), finalMax.score(), finalMax.reason());

        ReviewState issueState = new ReviewState(finalMax.type(), finalMax.score(), finalMax.reason());

        return policies.stream()
                .filter(p -> p.supports(issueState))
                .findFirst()
                .map(p -> p.apply(context, issueState))
                .orElse("I'm sorry, I didn't quite catch that. Could you say that again clearly?"); // 최후의 보루
    }
}