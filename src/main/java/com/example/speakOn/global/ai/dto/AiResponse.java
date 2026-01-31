package com.example.speakOn.global.ai.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "AI 대화 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AiResponse {

    @Schema(description = "AI의 텍스트 답변", example = "That's a great point. Can you elaborate on why you think so?")
    private String aiMessage;

    @Schema(description = "다음 질문 인덱스 (진행 상태)", example = "1")
    private Integer mainCount;

    @Schema(description = "다음 대화 깊이 (0:오프닝, 1:메인, 2:꼬리1, 3:꼬리2)", example = "2")
    private Integer depth;

    @Schema(description = "대화 종료 여부 (true일 경우 대화가 끝났음을 의미)", example = "false")
    @JsonProperty("isFinished")
    private boolean isFinished;
}