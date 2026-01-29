package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.global.ai.review.model.FailureType;

public record IssueScore(
        FailureType type,
        double score,
        String reason
) {}
