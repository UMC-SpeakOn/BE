package com.example.speakOn.domain.mySpeak.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TtsResponseDto {
    private String base64Audio;  // base64 인코딩된 mp3
}
