package com.example.speakOn.domain.mySpeak.dto.request;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationTurnRequest {

    private String languageCode = "en-US";

    @NotNull
    private MessageType messageType;
}
