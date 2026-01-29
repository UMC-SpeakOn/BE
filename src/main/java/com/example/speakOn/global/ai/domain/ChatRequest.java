package com.example.speakOn.global.ai.domain;

public record ChatRequest(

        Long myRoleId,
        int qCount,
        int depth,
        String userText


) {

    public static ChatRequest of(
            Long myRoleId,
            int qCount,
            int depth,
            String userText
    ) {
        return new ChatRequest(
                myRoleId,
                qCount,
                depth,
                userText
        );
    }

    public boolean isFirstQuestion() {
        return qCount == 0 && depth == 0;
    }

    public boolean isEmptyUtterance() {
        return userText == null || userText.isBlank();
    }
}
