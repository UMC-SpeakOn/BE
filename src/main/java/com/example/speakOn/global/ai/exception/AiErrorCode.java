package com.example.speakOn.global.ai.exception;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * AI 서비스 전용 에러 코드
 * 시스템 내부 오류와 외부 API(OpenAI 등) 오류 상황을 통합 관리합니다.
 */
@Getter
@AllArgsConstructor
public enum AiErrorCode implements BaseCode {

    // 400 Bad Request: 요청 데이터 오류 및 정책 위반
    AI_PROMPT_EMPTY(HttpStatus.BAD_REQUEST, "AI4001", "질문 내용이 비어있습니다."),
    AI_PROMPT_TOO_LONG(HttpStatus.BAD_REQUEST, "AI4002", "질문 내용이 너무 깁니다. (토큰 제한 초과)"),
    AI_CONTENT_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "AI4003", "AI 정책에 위배되는 내용이 포함되어 답변할 수 없습니다."),
    AI_RESPONSE_TRUNCATED(HttpStatus.BAD_REQUEST, "AI4004", "답변이 너무 길어 중단되었습니다. (Max Token 초과)"),

    // 401 Unauthorized: API 키 관련 오류
    AI_INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "AI4011", "AI 서비스 인증 키가 유효하지 않습니다."),

    // 404 Not Found: 모델 설정 오류
    AI_MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "AI4041", "지정된 AI 모델을 찾을 수 없습니다."),

    // 429 Too Many Requests: 요청 제한 및 결제 이슈
    AI_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI4291", "요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요."),
    AI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI4292", "AI 서비스 사용량 한도(요금)를 초과했습니다."),

    // 500 Internal Server Error: 서버 내부 로직 오류
    AI_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5001", "AI 응답을 처리하는 중 오류가 발생했습니다."),
    AI_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5002", "알 수 없는 AI 서비스 오류가 발생했습니다."),

    // 503 Service Unavailable: 외부 서비스 장애 및 통신 오류
    AI_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI5031", "AI 서버가 점검 중이거나 응답할 수 없는 상태입니다."),
    AI_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI5032", "AI 서버와 연결할 수 없습니다. 네트워크 상태를 확인해주세요."),

    // 504 Gateway Timeout: 시간 초과
    AI_RESPONSE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "AI5041", "AI 응답 시간이 초과되었습니다.");

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