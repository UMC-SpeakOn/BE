package com.example.speakOn.global.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "AI 대화 요청 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {

    @Schema(description = "사용자 역할 ID (MyRole PK)", example = "1")
    @NotNull(message = "myRoleId는 필수입니다.")
    private Long myRoleId;

    @Schema(description = "대화 세션 ID (질문 순서 랜덤 시드용)", example = "1")
    @NotNull(message = "sessionId는 필수입니다.")
    private Long sessionId;

    @Schema(description = "사용자의 발화 메시지", example = "I think communication is key.")
    @NotBlank(message = "userMessage는 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자를 넘을 수 없습니다.")
    private String userMessage;

    @Schema(description = "직전 AI의 답변 (대화 맥락 유지용, 첫 대화시 null 가능)", example = "Could you elaborate?")
    private String previousAiMessage;

    @Schema(description = "현재 질문 인덱스 (0부터 시작)", example = "0")
    @NotNull(message = "qCount는 필수입니다.")
    @Min(value = 0, message = "qCount는 0 이상이어야 합니다.")
    private Integer mainCount;

    @Schema(description = "대화 깊이 (0:오프닝, 1:메인, 2:꼬리1, 3:꼬리2)", example = "0")
    @NotNull(message = "depth는 필수입니다.")
    @Min(value = 0, message = "depth는 0 이상이어야 합니다.")
    private Integer depth;
}