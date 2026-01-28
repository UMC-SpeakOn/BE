package com.example.speakOn.global.ai.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class AiRequest {

    @NotNull(message = "myRoleId는 필수입니다.")
    private Long myRoleId;

    @NotBlank(message = "userMessage는 필수입니다.")
    @Size(max = 1000)
    private String userMessage;

    @NotNull(message = "qCount는 필수입니다.")
    @Min(0)
    @JsonProperty("qCount")
    private Integer qCount;

    @NotNull(message = "depth는 필수입니다.")
    @Min(0)
    private Integer depth;
}