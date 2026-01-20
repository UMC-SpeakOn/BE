package com.example.speakOn.global.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiRequestDto {

    @NotBlank(message = "systemMessage는 필수입니다.")
    @Size(max = 1000, message = "systemMessage는 1000자 이내여야 합니다.")
    private String systemMessage; // AI에게 부여할 역할 (예: 면접관)

    @NotBlank(message = "userMessage는 필수입니다.")
    @Size(max = 1000, message = "userMessage는 1000자 이내여야 합니다.")
    private String userMessage;   // 사용자 입력 메시지
}