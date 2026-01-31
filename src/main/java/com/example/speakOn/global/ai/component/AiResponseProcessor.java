package com.example.speakOn.global.ai.component;

import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.service.AiFallbackService;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiResponseProcessor {

    private final AiFallbackService aiFallbackService;

    public String processResponse(AiRequest request, ChatResponse response, String situationName) {
        // 1. 텍스트 추출
        String rawAiText = extractResponseText(response);

        // 2. 검토를 위한 Context 생성
        ChatRequest chatReq = ChatRequest.of(
                request.getMyRoleId(), request.getMainCount(), request.getDepth(), request.getUserMessage()
        );
        ChatContext context = ChatContext.of(chatReq, rawAiText);

        // 3. 시나리오 타입 매핑 (3가지 상황 처리)
        ScenarioType scenarioType = mapToScenarioType(situationName);

        // 4. 검토 및 수정(Fallback) 실행 -> 결과 반환
        return aiFallbackService.reviewAndCorrect(context, scenarioType);
    }

    private String extractResponseText(ChatResponse response) {
        return Optional.ofNullable(response)
                .map(ChatResponse::getResult).map(Generation::getOutput).map(AssistantMessage::getText)
                .filter(text -> !text.isBlank())
                .orElseThrow(() -> new GeneralException(AiErrorCode.AI_PARSE_ERROR));
    }

    private ScenarioType mapToScenarioType(String situationName) {
        if (situationName == null) {
            return ScenarioType.ONE_ON_ONE_MEETING;
        }

        String upperName = situationName.toUpperCase();

        if (upperName.contains("ONE_ON_ONE_MEETING") || upperName.contains("ONE_ON_ONE")) {
            return ScenarioType.ONE_ON_ONE_MEETING;
        } else if (upperName.contains("INTERVIEW")) {
            return ScenarioType.INTERVIEW;
        } else if (upperName.contains("MEETING")) {
            return ScenarioType.MEETING;
        } else {
            return ScenarioType.ONE_ON_ONE_MEETING;
        }
    }
}