package com.example.speakOn.global.ai.exception;

import com.example.speakOn.global.apiPayload.code.BaseCode;
import com.example.speakOn.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiErrorCode implements BaseCode {

    // =================================================================
    // [400 BAD REQUEST] : 잘못된 요청
    // =================================================================
    // 프롬프트가 없을 때
    AI_PROMPT_EMPTY(HttpStatus.BAD_REQUEST, "AI4001", "질문 내용이 비어있습니다."),
    // 질문이 너무 길 때 (토큰 제한 방지)
    AI_PROMPT_TOO_LONG(HttpStatus.BAD_REQUEST, "AI4002", "질문 내용이 너무 깁니다. (1000자 이내로 입력해주세요.)"),
    // OpenAI 정책 위반 (부적절한 콘텐츠)
    AI_CONTENT_POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "AI4003", "AI 정책에 위배되는 내용이 포함되어 있어 답변할 수 없습니다."),


    // =================================================================
    // [401 UNAUTHORIZED] : 인증 실패
    // =================================================================
    // API KEY가 없거나 틀렸을 때 (application.yml 설정 확인 필요)
    AI_INVALID_API_KEY(HttpStatus.UNAUTHORIZED, "AI4011", "AI 서비스 인증 키가 유효하지 않습니다. 관리자에게 문의하세요."),


    // =================================================================
    // [404 NOT FOUND] : 리소스 없음
    // =================================================================
    // 요청한 AI 모델(gpt-4o 등)이 존재하지 않거나 접근 불가능할 때
    AI_MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "AI4041", "지정된 AI 모델을 찾을 수 없습니다."),


    // =================================================================
    // [429 TOO MANY REQUESTS] : 요청 제한 및 요금 이슈
    // =================================================================
    // 단시간 과다 요청 (Rate Limit)
    AI_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI4291", "요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요."),
    // OpenAI 계정 잔액 부족 (Quota Exceeded) - 중요!
    AI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "AI4292", "AI 서비스 사용량 한도(요금)를 초과했습니다."),


    // =================================================================
    // [500 INTERNAL SERVER ERROR] : 서버 내부 오류
    // =================================================================
    // Spring AI 내부 파싱 에러
    AI_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5001", "AI 응답을 처리하는 중 오류가 발생했습니다."),
    // 알 수 없는 서버 에러 (Default)
    AI_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI5002", "알 수 없는 AI 서비스 오류가 발생했습니다."),


    // =================================================================
    // [503 SERVICE UNAVAILABLE] : 외부 서비스 장애
    // =================================================================
    // OpenAI 서버가 다운되었거나 응답하지 않을 때
    AI_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI5031", "AI 서버가 점검 중이거나 응답할 수 없는 상태입니다."),


    // =================================================================
    // [504 GATEWAY TIMEOUT] : 시간 초과
    // =================================================================
    // 응답이 너무 오래 걸릴 때
    AI_RESPONSE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "AI5041", "AI 응답 시간이 초과되었습니다. 다시 시도해주세요.");


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