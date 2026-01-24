package com.example.speakOn.global.ai.dto;

import com.example.speakOn.global.ai.review.ScenarioType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    // --- 대화 턴(도메인 ChatRequest 생성용) ---

    @NotBlank(message = "sessionId는 필수입니다.")
    @Size(max = 200, message = "sessionId는 200자 이내여야 합니다.")
    private String sessionId;     // 음성 회화 세션 ID

    @Min(value = 1, message = "turn은 1 이상이어야 합니다.")
    private int turn;             // 대화 턴 번호

    @NotBlank(message = "userText는 필수입니다.")
    @Size(max = 1000, message = "userText는 1000자 이내여야 합니다.")
    private String userText;      // STT 결과(없으면 userMessage와 동일하게 보내도 됨)

    @NotNull(message = "scenarioType은 필수입니다.")
    private ScenarioType scenarioType; // INTERVIEW / ONE_ON_ONE
}