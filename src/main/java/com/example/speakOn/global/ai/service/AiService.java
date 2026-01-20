package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.exception.AiResponseValidator;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
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

    public String callAi(Prompt prompt) {
        ChatResponse response = executeSafe(() -> chatModel.call(prompt));
        AiResponseValidator.validate(response);

        // Optional을 사용하여 안전하게 텍스트 추출
        return Optional.ofNullable(response.getResult().getOutput().getText())
                .filter(text -> !text.isBlank()) // 비어있지 않은지 한 번 더 확인
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