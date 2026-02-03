package com.example.speakOn.domain.avatar.enums;

import lombok.Getter;

@Getter
public enum SituationType {
    INTERVIEW("면접"),
    MEETING("회의"),
    ONE_ON_ONE_MEETING("1:1미팅");

    private final String description;

    SituationType(String description) {
        this.description = description;
    }

}
