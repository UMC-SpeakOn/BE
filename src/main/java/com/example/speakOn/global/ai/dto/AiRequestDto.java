package com.example.speakOn.global.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiRequestDto {
    private String systemMessage; // AI에게 부여할 역할 (예: 면접관)
    private String userMessage;   // 사용자 입력 메시지
}