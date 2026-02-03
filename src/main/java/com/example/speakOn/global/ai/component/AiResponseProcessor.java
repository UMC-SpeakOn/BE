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

    /**
     * AI 응답 텍스트 추출 -> 문맥 생성 -> 검토/수정(Fallback) 수행
     * * @param request AI 요청 정보 (User Message 등)
     * @param response LLM 응답 객체
     * @param situationName 상황 이름 (ScenarioType 매핑용)
     * @param currentMainCount 현재 메인 질문 카운트 (DB/Service에서 전달)
     * @param currentDepth 현재 꼬리질문 깊이 (DB/Service에서 전달)
     * @return 최종 검토된 AI 답변 문자열
     */
    public String processResponse(AiRequest request, ChatResponse response, String situationName,
                                  Integer currentMainCount, Integer currentDepth) {
        // 1. 텍스트 추출
        String rawAiText = extractResponseText(response);

        // 2. 검토를 위한 Context 생성
        ChatRequest chatReq = ChatRequest.of(
                request.getMyRoleId(),
                currentMainCount,
                currentDepth,
                request.getUserMessage()
        );
        ChatContext context = ChatContext.of(chatReq, rawAiText);

        // 3. 시나리오 타입 매핑
        ScenarioType scenarioType = mapToScenarioType(situationName);

        // 4. 검토 및 수정(Fallback) 실행 -> 결과 반환
        return aiFallbackService.reviewAndCorrect(context, scenarioType);
    }

    private String extractResponseText(ChatResponse response) {
        return Optional.ofNullable(response)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AssistantMessage::getText)
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