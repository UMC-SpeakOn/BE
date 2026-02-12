package com.example.speakOn.domain.mySpeak.dto.request;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationTurnRequest {

    private String languageCode = "en-US";

    @NotNull
    private MessageType messageType;
}
