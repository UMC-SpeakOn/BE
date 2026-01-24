package com.example.speakOn.global.ai.domain;

public record ChatRequest(

        String sessionId,   // 음성 회화 세션 ID
        int turn,           // 대화 턴 번호
        String userText    // STT 결과 (사용자 발화)


) {

    public static ChatRequest voice(
            String sessionId,
            int turn,
            String userText
    ) {
        return new ChatRequest(
                sessionId,
                turn,
                userText
        );
    }

    public boolean isFirstTurn() {
        return turn == 1;
    }

    public boolean isEmptyUtterance() {
        return userText == null || userText.isBlank();
    }
}
