package com.example.speakOn.global.ai.fallback.model;

public enum FailureType {
    NONE,           // 정상
    FLOW_ISSUE,     // 문맥 / 흐름 문제
    TONE_ISSUE,     // 비즈니스 톤 문제
    UNCLEAR         // 의미 불명확
}
