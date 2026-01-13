package com.example.speakOn.domain.mySpeak.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 'MAIN' 질문만 종료 카운트에 포함
@Getter
@AllArgsConstructor
public enum MessageType {
    MAIN("메인 질문"),
    FOLLOW("꼬리 질문"),
    CLOSING("마무리 멘트");

    private final String description;
}