package com.example.speakOn.global.ai.util;

import com.example.speakOn.global.apiPayload.code.BaseCode; // ✅ BaseCode Import 확인
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceExecutor {

    /**
     * ErrorStatus와 AiErrorCode 모두 BaseCode를 구현하고 있으므로,
     * 파라미터 타입을 BaseCode로 통일합니다.
     */
    public static <T> T executeSafe(CheckedSupplier<T> action, BaseCode errorCode) { // ✅ BaseCode로 변경
        try {
            return action.get();
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Service Execution Failed] Code: {}, Cause: {}", errorCode, e.getMessage(), e);
            throw new GeneralException(errorCode);
        }
    }
}