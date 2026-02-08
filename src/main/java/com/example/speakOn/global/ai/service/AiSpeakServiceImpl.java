package com.example.speakOn.global.ai.service;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.entity.Style;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.global.ai.component.*;
import com.example.speakOn.global.ai.dto.*;
import com.example.speakOn.global.ai.entity.AiConversationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSpeakServiceImpl implements AiSpeakService {

    private final ChatModel chatModel;
    private final AiContextService aiContextService;
    private final ServiceExecutor serviceExecutor;

    // Components
    private final AiDataReader dataReader;
    private final AiStateComponent stateComponent;
    private final AiPromptComponent promptComponent;
    private final AiResponseProcessor responseProcessor;

    @Override
    public String getOpener(Long myRoleId) {
        return serviceExecutor.executeSafe(() -> {
            MyRole myRole = dataReader.getMyRoleOrThrow(myRoleId);
            String personalGreeting = dataReader.getPersonalGreeting(myRole);
            String scenarioQuestion = stateComponent.safeGetEngineOpener(myRole.getSituation().name());

            if (personalGreeting == null || personalGreeting.isBlank()) {
                return scenarioQuestion;
            }
            return personalGreeting.trim() + " " + scenarioQuestion;
        });
    }

    @Transactional
    @Override
    public AiResponse chat(AiRequest request) {
        return serviceExecutor.executeSafe(() -> {

            // [1] 데이터 준비 및 상태 계산
            ConversationSession session = dataReader.getSessionOrThrow(request.getSessionId());
            MyRole myRole = dataReader.getMyRoleOrThrow(request.getMyRoleId());
            Avatar avatar = myRole.getAvatar();
            Style style = dataReader.getStyleOrThrow(avatar, myRole.getSituation());
            AiConversationContext aiContext = aiContextService.getOrCreateContext(session);

            String prevMessage = aiContext.getPreviousAiMessage();
            if (prevMessage == null || prevMessage.isBlank()) prevMessage = getOpener(request.getMyRoleId());

            ConversationState nextState = stateComponent.calculateNextState(
                    myRole, request.getUserMessage(), session.getCurrentQuestionCount(),
                    aiContext.getDepth(), session.getId(), request.getMessageType());

            // 종료 판정
            if (nextState.getMessageType() == MessageType.CLOSING) {
                return finalizeSession(session, aiContext, AiStateComponent.FIXED_CLOSING_MESSAGE);
            }

            // [2] LLM 호출
            Prompt prompt = promptComponent.createPrompt(myRole, avatar, style, request.getUserMessage(), prevMessage, nextState);
            ChatResponse response = chatModel.call(prompt);

            // [3] 응답 처리 및 조건부 Fallback
            String finalAiMessage = responseProcessor.processResponse(
                    request, response, myRole.getSituation(),
                    session.getCurrentQuestionCount(), nextState.getDepth());

            // [4] 마무리 및 DB 저장
            if (finalAiMessage != null && "[EXIT]".equals(finalAiMessage.trim())) {
                return finalizeSession(session, aiContext, AiStateComponent.FIXED_CLOSING_MESSAGE);
            }

            aiContextService.updateContext(aiContext.getId(), nextState.getDepth(), finalAiMessage);

            return AiResponse.builder()
                    .aiMessage(finalAiMessage)
                    .messageType(nextState.getMessageType())
                    .build();
        });
    }

    /**
     * 세션 종료 공통 로직 처리
     */
    @Transactional
    protected AiResponse finalizeSession(ConversationSession session, AiConversationContext aiContext, String closingMessage) {

        // 실제 소요 시간 계산 (현재 시간 - 시작 시간)
        long actualTotalTime = java.time.Duration.between(
                session.getStartedAt(),
                java.time.LocalDateTime.now()
        ).toSeconds();

        // 누적된 문장 수와 계산된 시간을 넘겨줌
        session.completeSession(
                (int) actualTotalTime,
                session.getSentenceCount(),
                java.time.LocalDateTime.now()
        );

        // 문맥 업데이트 (즉시 반영)
        aiContextService.updateContext(aiContext.getId(), aiContext.getDepth(), closingMessage);

        return AiResponse.builder()
                .aiMessage(closingMessage)
                .messageType(MessageType.CLOSING)
                .build();
    }
}