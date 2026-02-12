package com.example.speakOn.domain.mySpeak.dto.request;

import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TtsRequest {
    @NotBlank
    private String text; //AI가 생성한 질문

    private String voiceName; //TTS 음성 모델 이름.
    private Double speakingRate; //말하기 속도

    @NotNull
    private MessageType messageType;

    @NotNull
    private ConversationSession session;
}
