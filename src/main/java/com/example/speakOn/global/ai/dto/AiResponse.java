package com.example.speakOn.global.ai.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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

    private String aiMessage;

    @JsonProperty("qCount")
    private Integer qCount;

    private Integer depth;

    @JsonProperty("isFinished")
    private boolean isFinished;
}