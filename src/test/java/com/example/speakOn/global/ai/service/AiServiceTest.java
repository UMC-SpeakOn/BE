package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.fallback.AiFallbackHandler;
import com.example.speakOn.global.ai.review.ReviewEngine;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private ChatModel chatModel;
    private AiFallbackHandler aiFallbackHandler;
    private ReviewEngine reviewEngine;

    @Spy // 실제 로직을 타야하므로 Spy 사용
    private AiErrorConverter aiErrorConverter;

    @InjectMocks
    private AiService aiService;

    @Test
    @DisplayName("AI 호출 중 런타임 에러 발생 시 커스텀 예외로 변환되는지 테스트")
    void callAiWithException() {
        // given
        Prompt prompt = new Prompt(List.of(new UserMessage("test")));
        ChatRequest chatReq = ChatRequest.voice("session-1", 1, "test");
        when(chatModel.call(any(Prompt.class)))
                .thenThrow(new RuntimeException("OpenAI 429 quota_exceeded"));

        // when
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            aiService.callAi(chatReq, prompt, ScenarioType.INTERVIEW);
        });

        // then
        assertEquals(AiErrorCode.AI_QUOTA_EXCEEDED, exception.getCode());
    }
}