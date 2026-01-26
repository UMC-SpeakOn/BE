package com.example.speakOn.domain.myReport.code;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MyReportErrorCode implements BaseCode {

    // --- [400 BAD REQUEST] : 클라이언트의 요청이 유효하지 않음 ---
    // 소감 관련
    REFLECTION_TOO_LONG(HttpStatus.BAD_REQUEST, "REPORT4001", "사용자 소감은 최대 120자까지만 입력 가능합니다."),
    REFLECTION_EMPTY(HttpStatus.BAD_REQUEST, "REPORT4002", "소감 내용이 비어 있습니다."),

    INVALID_DIFFICULTY_RANGE(HttpStatus.BAD_REQUEST, "REPORT4009", "난이도는 1점에서 5점 사이의 정수만 입력 가능합니다."),

    // 필터링 및 페이징 관련
    INVALID_REPORT_FILTER(HttpStatus.BAD_REQUEST, "REPORT4003", "잘못된 직무 또는 상황 필터 조건입니다."),
    INVALID_PAGE_PARAMETER(HttpStatus.BAD_REQUEST, "REPORT4004", "페이지 번호는 0보다 커야 합니다."),

    // 비즈니스 로직 관련 (다시 듣기)
    REPLAY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "REPORT4005", "무료 다시 듣기 5회 횟수를 모두 소진하였습니다. 구독이 필요합니다."),

    // AI가 분석중인 경우
    REPORT_ANALYSIS_PENDING(HttpStatus.BAD_REQUEST, "REPORT4007", "AI가 아직 대화를 분석 중입니다. 잠시 후 다시 시도해 주세요."),

    // 분석할 내용이 없을 경우
    INSUFFICIENT_CHAT_DATA(HttpStatus.BAD_REQUEST, "REPORT4008", "대화 내용이 너무 짧아 리포트를 생성할 수 없습니다."),

    // --- [403 FORBIDDEN] : 인증은 되었으나, 해당 리소스에 접근 권한이 없음 ---
    REPORT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "REPORT4031", "접근 권한이 없습니다. 본인이 작성한 리포트만 조회 또는 수정할 수 있습니다."),

    // --- [404 NOT FOUND] : 요청한 리소스를 찾을 수 없음 ---
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4041", "해당 ID의 리포트를 찾을 수 없습니다."),
    SESSION_NOT_FOUND_FOR_REPORT(HttpStatus.NOT_FOUND, "REPORT4042", "리포트와 연결된 대화 세션 데이터가 유실되어 정보를 불러올 수 없습니다."),
    CONVERSATION_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4043", "해당 리포트의 대화 로그 데이터가 존재하지 않습니다."),

    // DB에는 URL이 있는데 실제 S3 파일이 없을 때
    AUDIO_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4045", "음성 파일을 찾을 수 없습니다. (관리자에게 문의해주세요.)"),

    // --- [500 INTERNAL SERVER ERROR] : 서버 내부 오류 (AI 관련) ---
    AI_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "REPORT5001", "AI 분석 과정에서 오류가 발생하여 리포트를 완료하지 못했습니다.");

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