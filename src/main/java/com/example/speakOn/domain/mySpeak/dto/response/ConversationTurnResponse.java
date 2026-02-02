package com.example.speakOn.domain.mySpeak.dto.response;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationTurnResponse {
    private String questionText;
    private String base64Audio;  // base64 인코딩된 mp3
    private MessageType messageType;
}
