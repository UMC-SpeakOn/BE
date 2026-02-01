package com.example.speakOn.domain.mySpeak.dto.request;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import jakarta.validation.constraints.NotNull;

public class ConversationTurnTextRequest {
    private String languageCode = "en-US";

    @NotNull
    private String anwerText;

    @NotNull
    private MessageType messageType;
}
