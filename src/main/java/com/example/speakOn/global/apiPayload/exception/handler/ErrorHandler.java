package com.example.speakOn.global.apiPayload.exception.handler;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseCode errorCode) {
        super(errorCode);
    }
}

