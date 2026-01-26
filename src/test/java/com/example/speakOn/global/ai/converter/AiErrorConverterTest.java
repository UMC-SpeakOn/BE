package com.example.speakOn.global.ai.converter;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiErrorConverterTest {

    private final AiErrorConverter converter = new AiErrorConverter();

    @Test
    @DisplayName("OpenAI 요금 부족 메시지 분석 테스트")
    void convertQuotaError() {
        // given
        Exception ex = new RuntimeException("Error 429: Your quota is exceeded. Please check your billing.");

        // when
        AiErrorCode result = converter.convert(ex);

        // then
        assertEquals(AiErrorCode.AI_QUOTA_EXCEEDED, result);
    }

    @Test
    @DisplayName("네트워크 연결 실패 예외 타입 테스트")
    void convertConnectionError() {
        // given
        Exception ex = new ResourceAccessException("I/O error", new ConnectException("Connection refused"));

        // when
        AiErrorCode result = converter.convert(ex);

        // then
        assertEquals(AiErrorCode.AI_CONNECTION_ERROR, result);
    }

    @Test
    @DisplayName("API 키 인증 실패 메시지 분석 테스트")
    void convertAuthError() {
        // given
        Exception ex = new RuntimeException("Incorrect API key provided. (401)");

        // when
        AiErrorCode result = converter.convert(ex);

        // then
        assertEquals(AiErrorCode.AI_INVALID_API_KEY, result);
    }
}
