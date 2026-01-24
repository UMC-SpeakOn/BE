package com.example.speakOn.global.ai.review;

import com.example.speakOn.global.ai.review.model.FailureType;

public record IssueScore(
        FailureType type,
        double score,
        String reason
) {}
