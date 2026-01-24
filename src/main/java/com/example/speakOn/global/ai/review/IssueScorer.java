package com.example.speakOn.global.ai.review;

import com.example.speakOn.global.ai.fallback.policy.ChatContext;

public interface IssueScorer {
    IssueScore score(ChatContext context, ScenarioType scenario);
}
