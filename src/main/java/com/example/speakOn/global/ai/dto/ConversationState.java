package com.example.speakOn.global.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationState {

    private final int qCount;
    private final int depth;
    private final String instruction;
    private final boolean isFinished;

}