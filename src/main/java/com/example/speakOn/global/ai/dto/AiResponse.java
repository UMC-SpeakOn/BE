package com.example.speakOn.global.ai.dto;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "AI 대화 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {

    @Schema(description = "AI의 텍스트 답변")
    private String aiMessage;

    @Schema(description = "AI 질문 타입 (MAIN, FOLLOW, CLOSING)")
    private MessageType messageType;
}