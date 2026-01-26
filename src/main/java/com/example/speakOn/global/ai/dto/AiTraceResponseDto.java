package com.example.speakOn.global.ai.dto;

import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.review.model.FailureType;

public record AiTraceResponseDto(
        String finalText,
        String originalText,

        boolean technicalFallbackApplied,
        AiErrorCode technicalErrorCode,

        boolean qualityFallbackApplied,
        FailureType failureType,
        double confidence,
        String reason,
        String appliedPolicy
) {}