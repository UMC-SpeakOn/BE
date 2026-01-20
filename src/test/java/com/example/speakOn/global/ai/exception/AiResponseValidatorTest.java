package com.example.speakOn.global.ai.exception;

import com.example.speakOn.global.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AiResponseValidatorTest {

    @Test
    @DisplayName("응답이 null일 때 AI_PARSE_ERROR 발생")
    void validateNullResponse() {
        GeneralException exception = assertThrows(GeneralException.class, () ->
                AiResponseValidator.validate(null)
        );
        assertEquals(AiErrorCode.AI_PARSE_ERROR, exception.getCode());
    }

    @Test
    @DisplayName("FinishReason이 length일 때 AI_RESPONSE_TRUNCATED 발생")
    void validateLengthReason() {
        // given: finishReason이 "length"인 Mock 객체 생성
        ChatResponse mockResponse = createMockResponse("length");

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                AiResponseValidator.validate(mockResponse)
        );
        assertEquals(AiErrorCode.AI_RESPONSE_TRUNCATED, exception.getCode());
    }

    @Test
    @DisplayName("FinishReason이 content_filter일 때 AI_CONTENT_POLICY_VIOLATION 발생")
    void validateFilterReason() {
        // given: finishReason이 "content_filter"인 Mock 객체 생성
        ChatResponse mockResponse = createMockResponse("content_filter");

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                AiResponseValidator.validate(mockResponse)
        );
        assertEquals(AiErrorCode.AI_CONTENT_POLICY_VIOLATION, exception.getCode());
    }

    /**
     * 테스트용 Mock 응답 객체를 생성하는 헬퍼 메서드
     */
    private ChatResponse createMockResponse(String finishReason) {
        ChatResponse response = mock(ChatResponse.class);
        Generation generation = mock(Generation.class);
        ChatGenerationMetadata metadata = mock(ChatGenerationMetadata.class);

        // response.getResult() -> generation
        when(response.getResult()).thenReturn(generation);
        // generation.getOutput() -> null이 아니어야 함
        when(generation.getOutput()).thenReturn(mock(org.springframework.ai.chat.messages.AssistantMessage.class));
        // generation.getMetadata() -> metadata
        when(generation.getMetadata()).thenReturn(metadata);
        // metadata.getFinishReason() -> 우리가 테스트하려는 사유
        when(metadata.getFinishReason()).thenReturn(finishReason);

        return response;
    }
}