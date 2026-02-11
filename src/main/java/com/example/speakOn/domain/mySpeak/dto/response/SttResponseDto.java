package com.example.speakOn.domain.mySpeak.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SttResponseDto {
    private String transcript; //변환된 텍스트
}
