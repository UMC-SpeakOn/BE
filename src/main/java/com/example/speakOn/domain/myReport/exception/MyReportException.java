package com.example.speakOn.domain.myReport.exception;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.exception.GeneralException;

public class MyReportException extends GeneralException {
    public MyReportException(BaseCode code) {
        super(code);
    }
}