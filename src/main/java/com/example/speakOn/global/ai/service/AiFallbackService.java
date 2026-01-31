package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.fallback.policy.FallbackPolicy;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import com.example.speakOn.global.ai.review.scorer.IssueScore;
import com.example.speakOn.global.ai.review.scorer.IssueScorer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFallbackService {

    private final List<IssueScorer> scorers;     // 채점 위원들
    private final List<FallbackPolicy> policies; // 대처 방안들

    /**
     * AI 답변을 검토하고, 문제가 심각하면 폴백 메시지로 교체합니다.
     * @return 문제가 없으면 원본 텍스트, 있으면 폴백 텍스트
     */
    public String reviewAndCorrect(ChatContext context, ScenarioType scenario) {

        // 1. [검토] 모든 Scorer를 돌며 가장 높은 문제 점수 계산
        IssueScore maxScore = new IssueScore(FailureType.NONE, 0.0, "ok");

        for (IssueScorer scorer : scorers) {
            IssueScore score = scorer.score(context, scenario);
            if (score.score() > maxScore.score()) {
                maxScore = score;
            }
        }

        // 2. [판단] 임계값(0.6) 미만이면 통과 -> 원본 반환
        if (maxScore.score() < 0.6) {
            return context.originalText();
        }

        // 3. [조치] 임계값 초과 시 ReviewState 생성
        log.warn("[AI Fallback] Issue Detected: type={}, score={}, reason={}",
                maxScore.type(), maxScore.score(), maxScore.reason());

        ReviewState issueState = new ReviewState(maxScore.type(), maxScore.score(), maxScore.reason());

        // 4. [대체] 적절한 정책(Policy) 찾아서 적용
        IssueScore finalMaxScore = maxScore;
        return policies.stream()
                .filter(p -> p.supports(issueState))
                .findFirst()
                .map(p -> p.apply(context, issueState))
                .orElseGet(() -> defaultMessage(finalMaxScore.type())); // 정책 없으면 기본 메시지
    }

    private String defaultMessage(FailureType type) {
        return switch (type) {
            case FLOW_ISSUE -> "Let’s get back on track. Could you answer in one clear sentence?";
            case TONE_ISSUE -> "Let’s keep it professional. Could you rephrase that in a more business-appropriate tone?";
            case UNCLEAR -> "I didn’t quite understand. Could you rephrase it more clearly?";
            default -> "Sorry, I didn't catch that. Could you say it again?";
        };
    }
}