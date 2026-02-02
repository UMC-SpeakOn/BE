package com.example.speakOn.global.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * AI 페르소나 생성을 위한 프롬프트 변수 DTO
 */
@Getter
@Builder
public class PromptVariables {

    @NotBlank(message = "아바타 이름은 필수 값입니다.")
    private String name;

    @NotBlank(message = "아바타 직업은 필수 값입니다.")
    private String job;

    @NotBlank(message = "대화 상황은 필수 값입니다.")
    private String situation;

    @NotBlank(message = "국적 정보는 필수 값입니다.")
    private String nationality;

    @NotBlank(message = "언어 설정 값은 필수 값입니다.")
    private String locale;

    @NotBlank(message = "성별 정보는 필수 값입니다.")
    private String gender;

    @NotBlank(message = "말하기 스타일은 필수 값입니다.")
    private String speechStyle;

}