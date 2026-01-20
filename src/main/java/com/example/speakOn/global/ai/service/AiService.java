package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.converter.AiErrorConverter;
import com.example.speakOn.global.ai.exception.AiResponseValidator;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;
    private final AiErrorConverter aiErrorConverter;

    public String callAi(Prompt prompt) {
        // 1. 모델 호출 및 네트워크/외부 에러 변환 실행
        ChatResponse response = executeSafe(() -> chatModel.call(prompt));

        // 2. 응답 데이터 논리 검증
        AiResponseValidator.validate(response);
        return response.getResult().getOutput().getText();
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