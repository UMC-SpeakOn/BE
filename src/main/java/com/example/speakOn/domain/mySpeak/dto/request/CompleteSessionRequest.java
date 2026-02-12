package com.example.speakOn.domain.mySpeak.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompleteSessionRequest {
    @NotNull
    private LocalDateTime endedAt;       // 현재 시간

    @NotNull
    private Integer totalTime;           // 실제 대화 시간 (일시정지 제외)
}
