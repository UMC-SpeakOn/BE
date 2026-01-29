package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;

public interface IssueScorer {
    IssueScore score(ChatContext context, ScenarioType scenario);
}
