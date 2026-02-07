package com.example.speakOn.global.ai.exception;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiErrorCode implements BaseCode {

    // [400] 잘못된 요청 및 정책 위반
    AI_PROMPT_EMPTY(HttpStatus.BAD_REQUEST, "AI4001", "질문 내용이 비어있습니다."),
    AI_PROMPT_TOO_LONG(HttpStatus.BAD_REQUEST, "AI4002", "질문 내용이 너무 깁니다."),
    AI_CONTENT_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "AI4003", "AI 정책 위반 내용이 포함되어 있습니다."),
    AI_RESPONSE_TRUNCATED(HttpStatus.BAD_REQUEST, "AI4004", "답변이 너무 길어 중단되었습니다. (Max Token 초과)"),
    AI_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "AI4005", "잘못된 요청 데이터입니다."),

    // [401] 인증 실패
    AI_INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "AI4011", "AI 인증 키가 유효하지 않습니다."),

    // [404] 데이터 없음
    AI_MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "AI4041", "AI 모델을 찾을 수 없습니다."),
    AI_STYLE_NOT_FOUND(HttpStatus.NOT_FOUND, "AI4042", "해당 아바타와 상황에 맞는 스타일 설정을 찾을 수 없습니다."),
    AI_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "AI4049", "필수 데이터(세션/역할)가 DB에 존재하지 않습니다."),

    // [429] 사용량 초과
    AI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI4292", "사용량 한도를 초과했습니다."),

    // [500] 서버 내부 및 데이터 처리 오류
    AI_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5001", "응답 데이터 파싱 오류."),
    AI_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5002", "알 수 없는 서버 오류."),
    AI_DB_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5003", "DB 데이터 처리 오류 (제약조건/정합성)."),
    AI_DB_SCHEMA_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5004", "DB 스키마 오류 (테이블/컬럼 없음, SQL 문법)."),
    AI_NO_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "AI5005", "빈 응답이 반환되었습니다."),

    // [503] 인프라 및 연결 장애
    AI_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI5032", "외부 AI 서비스 연결 실패."),
    AI_DB_CONNECTION_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "AI5033", "DB 연결 실패 (Connection Refused/Pool Full)."),
    AI_DB_TRANSACTION_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "AI5034", "DB 응답 시간 초과."),
    AI_DB_DEADLOCK(HttpStatus.SERVICE_UNAVAILABLE, "AI5035", "DB 트랜잭션 충돌(Deadlock)이 발생했습니다."),

    // [504] 타임아웃
    AI_RESPONSE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "AI5041", "응답 시간이 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder().message(message).code(code).isSuccess(false).httpStatus(httpStatus).build();
    }
}