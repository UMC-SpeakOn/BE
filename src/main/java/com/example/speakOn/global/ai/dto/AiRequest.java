package com.example.speakOn.global.ai.dto;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.global.ai.domain.ChatRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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

    private MessageType messageType;

    public ChatRequest toChatRequest(int qCount, int depth) {
        return new ChatRequest(
                this.myRoleId,
                qCount,
                depth,
                this.userMessage
        );
    }
}

