package com.example.speakOn.global.ai.component;

import com.example.speakOn.domain.avatar.enums.SituationType;
import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
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

    public String processResponse(AiRequest request, ChatResponse response, SituationType situation,
                                  Integer currentMainCount, Integer currentDepth) {

        String rawAiText = extractResponseText(response);

        // [1] [EXIT] 플래그 감지 시 즉시 반환
        if (rawAiText.trim().equals("[EXIT]")) return "[EXIT]";

        // [2] 품질 검증 후 필요한 경우에만 Fallback 실행
        if (needsReview(rawAiText)) {
            log.info("[Fallback] Quality check failed. Correcting response...");
            ChatRequest chatReq = ChatRequest.of(request.getMyRoleId(), currentMainCount, currentDepth, request.getUserMessage());

            ChatContext context = ChatContext.of(chatReq, rawAiText, situation);
            return aiFallbackService.reviewAndCorrect(context);
        }

        return rawAiText;
    }

    private boolean needsReview(String text) {
        return text.length() < 10 || text.toLowerCase().contains("ai assistant");
    }

    private String extractResponseText(ChatResponse response) {
        return Optional.ofNullable(response)
                .map(ChatResponse::getResult).map(Generation::getOutput).map(AssistantMessage::getText)
                .filter(text -> !text.isBlank())
                .orElseThrow(() -> new GeneralException(AiErrorCode.AI_PARSE_ERROR));
    }

}