package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.ai.domain.ChatRequest;
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


}