package com.example.speakOn.domain.mySpeak.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompleteSessionResponse {

    private Long sessionId;
    private Integer totalTime; // 총 시간
    private Integer sentenceCount; //문장 수
    private String closingTtsBase64; // tts 로 변환된 마무리 멘트
    private String closingText;
}
