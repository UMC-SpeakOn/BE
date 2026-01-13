package com.example.speakOn.domain.mySpeak.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 발화의 성격을 구분합니다.
 * 와이어프레임 로직상 'MAIN' 질문만 종료 카운트에 포함됩니다.
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    MAIN("메인 질문"),
    FOLLOW("꼬리 질문"),
    CLOSING("마무리 멘트");

    private final String description;
}