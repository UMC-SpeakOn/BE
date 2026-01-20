package com.example.speakOn.domain.myReport.code;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyReportErrorCode implements BaseCode {

    // --- [400 BAD REQUEST] ---
    // 1. 소감 관련
    REFLECTION_TOO_LONG(HttpStatus.BAD_REQUEST, "REPORT4001", "사용자 소감은 최대 120자까지만 입력 가능합니다."),
    REFLECTION_EMPTY(HttpStatus.BAD_REQUEST, "REPORT4002", "소감 내용이 비어 있습니다."),

    // 2. 필터링 및 파라미터 관련
    INVALID_REPORT_FILTER(HttpStatus.BAD_REQUEST, "REPORT4003", "잘못된 직무 또는 상황 필터 조건입니다."),
    INVALID_PAGE_PARAMETER(HttpStatus.BAD_REQUEST, "REPORT4004", "페이지 번호는 0보다 커야 합니다."),

    // 3. 비즈니스 로직 관련 (다시 듣기)
    REPLAY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "REPORT4005", "무료 다시 듣기 5회 횟수를 모두 소진하였습니다. 구독이 필요합니다."),

    // --- [403 FORBIDDEN] ---
    REPORT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REPORT4031", "본인이 작성한 리포트만 조회 또는 수정할 수 있습니다."),

    // --- [404 NOT FOUND] ---
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4041", "해당 ID의 리포트를 찾을 수 없습니다."),
    SESSION_NOT_FOUND_FOR_REPORT(HttpStatus.NOT_FOUND, "REPORT4042", "리포트와 연결된 대화 세션 정보를 찾을 수 없습니다.");

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