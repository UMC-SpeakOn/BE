package com.example.speakOn.global.ai.converter;

import com.example.speakOn.global.ai.exception.AiErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Component
public class AiErrorConverter {

    public AiErrorCode convert(Exception e) {
        if (e == null || e.getMessage() == null) return AiErrorCode.AI_SERVER_ERROR;

        AiErrorCode errorCode = switch (e) {
            case ResourceAccessException rae -> AiErrorCode.AI_CONNECTION_ERROR;
            case ConnectException ce -> AiErrorCode.AI_CONNECTION_ERROR;
            case SocketTimeoutException ste -> AiErrorCode.AI_RESPONSE_TIMEOUT;
            case Exception ex when contains(ex, "401", "api_key") -> AiErrorCode.AI_INVALID_API_KEY;
            case Exception ex when contains(ex, "quota", "insufficient") -> AiErrorCode.AI_QUOTA_EXCEEDED;
            case Exception ex when contains(ex, "503", "500", "server_error") -> AiErrorCode.AI_SERVER_ERROR;
            default -> AiErrorCode.AI_SERVER_ERROR;
        };

        // 실무 포인트: 변환된 에러와 원본 메시지를 로그로 남겨 추적 가능하게 함
        log.error("[AI_CONVERTER] 외부 예외 발생 -> 매핑 코드: {}, 원본 메시지: {}", errorCode.getCode(), e.getMessage());
        return errorCode;
    }

    private boolean contains(Exception e, String... keywords) {
        String msg = e.getMessage().toLowerCase();
        for (String keyword : keywords) {
            if (msg.contains(keyword.toLowerCase())) return true;
        }
        return false;
    }
}