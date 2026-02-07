package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.fallback.policy.FallbackPolicy;
import com.example.speakOn.global.ai.review.model.FailureType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import com.example.speakOn.global.ai.review.scorer.IssueScore;
import com.example.speakOn.global.ai.review.scorer.IssueScorer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiFallbackService {

    private final ChatModel chatModel;
    private final List<IssueScorer> scorers;
    private final List<FallbackPolicy> policies;

    /**
     * [1] AI 답변 검토 및 수정 (Post-Processing)
     */
    public String reviewAndCorrect(ChatContext context) {
        IssueScore maxScore = new IssueScore(FailureType.NONE, 0.0, "ok");

        // 1. [검토]
        for (IssueScorer scorer : scorers) {
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

    /**
     * [2] 종료 의도 파악 (Intent Check) - ConversationEngine에서 호출
     * LLM을 사용하여 유저가 대화를 끝내고 싶어하는지 "TRUE/FALSE"로 판단
     */
    public boolean checkExitIntent(String userMessage, String promptTemplate) {
        try {
            // 1. 프롬프트에 유저 메시지 주입
            String fullPrompt = promptTemplate.replace("{input}", userMessage);

            // 2. LLM 호출
            String response = chatModel.call(new Prompt(fullPrompt))
                    .getResult()
                    .getOutput()
                    .getText()
                    .trim()
                    .toUpperCase();

            // 3. 결과 판단
            log.info("[ExitIntent] LLM Check Result: {}", response);
            return response.contains("TRUE");

        } catch (Exception e) {
            log.error("[ExitIntent] LLM Check Failed", e);
            return false;
        }
    }
}