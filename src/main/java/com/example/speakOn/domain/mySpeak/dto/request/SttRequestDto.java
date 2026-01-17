package com.example.speakOn.domain.mySpeak.dto.request;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class SttRequestDto {

    private String languageCode = "en-US";

    @NonNull
    private MessageType messageType;

    @NotNull
    private Long sessionId;         // ConversationSession ID (보안)
}
