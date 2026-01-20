package com.example.speakOn.domain.mySpeak.exception;

import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.exception.GeneralException;

public class MySpeakException extends GeneralException {

    public MySpeakException(MySpeakErrorCode errorCode) {
        super(errorCode);
    }

    public MySpeakException(BaseCode code) {
        super(code);
    }
}
