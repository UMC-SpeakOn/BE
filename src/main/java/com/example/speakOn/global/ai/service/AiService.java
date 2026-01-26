package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.dto.AiTraceResponseDto;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.exception.AiResponseValidator;
import com.example.speakOn.global.ai.exception.AiValidationResult;
import com.example.speakOn.global.ai.fallback.AiFallbackHandler;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ReviewEngine;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.ReviewState;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;
    private final AiErrorConverter aiErrorConverter;
    private final AiFallbackHandler aiFallbackHandler;
    private final ReviewEngine reviewEngine;

    public String callAi(ChatRequest chatReq, Prompt prompt, ScenarioType scenarioType) {

        ChatResponse response = executeSafe(() -> chatModel.call(prompt));

        AiValidationResult validation = AiResponseValidator.validate(response);

        if (!validation.valid()) {
            log.warn("AI validation failed: {}", validation.errorCode());
            return aiFallbackHandler.handle(prompt, response, validation);
        }

        String original = extractText(response);

        ChatContext context = ChatContext.of(chatReq, original);
        ReviewState state = reviewEngine.review(context, scenarioType);

        if (state.isOk()) {
            return original;
        }

        return aiFallbackHandler.handle(context, state);
    }

    private String extractText(ChatResponse response) {
        return Optional.ofNullable(response.getResult().getOutput().getText())
                .filter(text -> !text.isBlank())
                .orElseThrow(() -> new GeneralException(AiErrorCode.AI_PARSE_ERROR));
    }

    private <T> T executeSafe(Supplier<T> action) {
        try {
            return action.get();
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            // 모든 런타임 예외를 Converter를 통해 시스템 규격 에러로 변환
            throw new GeneralException(aiErrorConverter.convert(e));
        }
    }

    public AiTraceResponseDto callAiTrace(ChatRequest chatReq, Prompt prompt, ScenarioType scenarioType) {

        ChatResponse response = executeSafe(() -> chatModel.call(prompt));

        AiValidationResult validation = AiResponseValidator.validate(response);

        if (!validation.valid()) {
            String finalText = aiFallbackHandler.handle(prompt, response, validation);

            return new AiTraceResponseDto(
                    finalText,
                    null,
                    true,
                    validation.errorCode(),
                    false,
                    null,
                    0.0,
                    "validation failed",
                    "TECHNICAL_FALLBACK"
            );
        }

        String original = extractText(response);

        ChatContext context = ChatContext.of(chatReq, original);
        ReviewState state = reviewEngine.review(context, scenarioType);

        // quality fallback 적용 여부 판단(핸들러 로직과 동일하게)
        // (권장: threshold는 handler 내부와 동일하게 유지)
        double threshold = (scenarioType == ScenarioType.ONE_ON_ONE) ? 0.55 : 0.60;
        boolean qualityApplied = !state.isOk() && state.confidence() >= threshold;

        // appliedPolicy 이름까지 받고 싶으면 handler가 policy 선택 결과를 리턴하도록 개선해야 하는데,
        // 일단은 간단히 failureType 기준으로 표시
        String appliedPolicy = qualityApplied ? ("QUALITY_" + state.failureType()) : "NONE";

        String finalText = qualityApplied
                ? aiFallbackHandler.handle(context, state)
                : original;

        return new AiTraceResponseDto(
                finalText,
                original,
                false,
                null,
                qualityApplied,
                state.failureType(),
                state.confidence(),
                state.reason(),
                appliedPolicy
        );
    }


}