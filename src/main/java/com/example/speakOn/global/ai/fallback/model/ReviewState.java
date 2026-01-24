package com.example.speakOn.global.ai.fallback.model;

public record ReviewState(
        FailureType failureType,
        double confidence,      // 이 판단에 대한 확신도
        String reason           // 디버깅 / 로그용
) {

    public boolean isOk() {
        return failureType == FailureType.NONE;
    }
}

