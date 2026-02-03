package com.example.speakOn.domain.myRole.enums;

import lombok.Getter;

@Getter
public enum JobType {
    MARKETING("마케팅"),
    DEVELOPMENT("개발"),
    DESIGN("디자인"),
    PLANNING("기획"),
    SALES("영업"),
    BUSINESS("일반 비지니스");

    private final String description;

    JobType(String description) {
        this.description = description;
    }
}
