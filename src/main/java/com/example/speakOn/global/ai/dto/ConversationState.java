package com.example.speakOn.global.ai.dto;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationState {

    private final Integer mainCount;
    private final Integer depth;
    private final String instruction;
    private final Boolean isFinished;
    private MessageType messageType;

}