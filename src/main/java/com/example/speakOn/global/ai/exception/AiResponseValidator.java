package com.example.speakOn.global.ai.exception;

import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;

@Slf4j
public class AiResponseValidator {

    public static void validate(ChatResponse response) {
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            log.error("[AI_VALIDATOR] 응답 데이터가 비어있거나 파싱할 수 없음");
            throw new GeneralException(AiErrorCode.AI_PARSE_ERROR);
        }

        if (response.getResult().getMetadata() != null) {
            String finishReason = response.getResult().getMetadata().getFinishReason();

            if ("length".equalsIgnoreCase(finishReason)) {
                log.warn("[AI_VALIDATOR] 답변 중단: 토큰 제한 초과");
                throw new GeneralException(AiErrorCode.AI_RESPONSE_TRUNCATED);
            }
            if ("content_filter".equalsIgnoreCase(finishReason)) {
                log.warn("[AI_VALIDATOR] 답변 중단: 부적절한 콘텐츠 차단");
                throw new GeneralException(AiErrorCode.AI_CONTENT_POLICY_VIOLATION);
            }
        }
    }
}