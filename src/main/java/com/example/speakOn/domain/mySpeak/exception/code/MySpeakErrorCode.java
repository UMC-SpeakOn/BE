package com.example.speakOn.domain.mySpeak.exception.code;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MySpeakErrorCode implements BaseCode {

    // 사용자 검증 (400)
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "MS4001", "유효하지 않은 사용자 ID입니다"),

    // MyRole 관련 (404)
    NO_MYROLES_AVAILABLE(HttpStatus.NOT_FOUND, "MS4002", "이용가능한 역할(MyRole)이 없습니다"),

    // DB 조회 실패 (500)
    MYROLE_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MS5001", "사용자 역할 조회 중 오류가 발생했습니다"),

    // 변환 실패 (500)
    MYROLE_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MS5002", "사용자 역할 데이터 변환 실패"),

    // 대기화면 로드 (500)
    WAIT_SCREEN_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MS5003", "대기화면 로드 실패");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}

