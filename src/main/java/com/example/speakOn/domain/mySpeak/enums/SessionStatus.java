package com.example.speakOn.domain.mySpeak.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionStatus {
    ONGOING("대화 중"),
    COMPLETED("종료됨"),
    CANCELED("중단됨");

    private final String description;
}