package com.example.speakOn.domain.myReport.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ToneType {
    //Text -based
    CASUAL, NEUTRAL, PROFESSIONAL;

    @JsonCreator
    public static ToneType fromString(String value) {
        if (value == null) return NEUTRAL;
        try {
            return ToneType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NEUTRAL;
        }
    }
}
