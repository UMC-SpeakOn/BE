package com.example.speakOn.global.ai.exception;

public record AiValidationResult(
        boolean valid,
        AiErrorCode errorCode
) {
    public static AiValidationResult ok() {
        return new AiValidationResult(true, null);
    }

    public static AiValidationResult fail(AiErrorCode errorCode) {
        return new AiValidationResult(false, errorCode);
    }
}
