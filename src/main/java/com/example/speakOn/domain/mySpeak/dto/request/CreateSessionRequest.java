package com.example.speakOn.domain.mySpeak.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class CreateSessionRequest {

    @NotNull(message = "myRoleId는 필수입니다")
    private Long myRoleId;

    @NotNull(message = "targetQuestionCount는 필수입니다")
    @Min(value = 1, message = "질문 개수는 1 이상이어야 합니다")
    private Integer targetQuestionCount;

    @NotNull(message = "startedAt는 필수입니다")
    private LocalDateTime startedAt;
}

