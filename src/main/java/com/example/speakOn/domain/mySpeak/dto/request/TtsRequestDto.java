package com.example.speakOn.domain.mySpeak.dto.request;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TtsRequestDto {
    @NotBlank
    private String text; //AI가 생성한 질문

    private String voiceName = "en-US-Neural2-F"; //TTS 음성 모델 이름.
    private Double speakingRate = 1.0; //말하기 속도

    @NotNull
    private MessageType messageType;

    @NotNull
    private Long sessionId;
}
