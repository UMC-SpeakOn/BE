package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.component.*;
import com.example.speakOn.global.ai.dto.*;
import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.entity.Style;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.entity.ConversationSession;
import com.example.speakOn.global.ai.entity.AiConversationContext;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.util.ServiceExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSpeakServiceImpl implements AiSpeakService {

    private final ChatModel chatModel;
    private final AiContextService aiContextService;

    // Components
    private final AiDataReader dataReader;
    private final AiStateComponent stateComponent;
    private final AiPromptComponent promptComponent;
    private final AiResponseProcessor responseProcessor;

    @Override
    public String getOpener(Long myRoleId) {
        return ServiceExecutor.executeSafe(() -> {
            MyRole myRole = dataReader.getMyRoleOrThrow(myRoleId);
            String personalGreeting = dataReader.getPersonalGreeting(myRole);
            String scenarioQuestion = stateComponent.safeGetEngineOpener(myRole.getSituation().name());

            if (personalGreeting.isBlank()) return scenarioQuestion;
            return personalGreeting.trim() + " " + scenarioQuestion;
        }, AiErrorCode.AI_SERVER_ERROR);
    }

    @Override
    public AiResponse chat(AiRequest request) {
        return ServiceExecutor.executeSafe(() -> {
            // [1] 데이터 조회
            ConversationSession session = dataReader.getSessionOrThrow(request.getSessionId());
            MyRole myRole = dataReader.getMyRoleOrThrow(request.getMyRoleId());
            Avatar avatar = myRole.getAvatar();
            Style style = dataReader.getStyleOrThrow(avatar, myRole.getSituation());

            // [2] Context 조회/생성
            AiConversationContext aiContext = aiContextService.getOrCreateContext(session);

            // [3] 이전 문맥 준비
            String prevMessage = aiContext.getPreviousAiMessage();
            if (prevMessage == null || prevMessage.isBlank()) {
                prevMessage = getOpener(request.getMyRoleId());
            }

            // [4] 상태 계산
            ConversationState nextState = stateComponent.calculateNextState(
                    myRole,
                    request.getUserMessage(),
                    session.getCurrentQuestionCount(),
                    aiContext.getDepth(),
                    session.getId()
            );

            // [5] LLM 호출
            Prompt prompt = promptComponent.createPrompt(
                    myRole, avatar, style,
                    request.getUserMessage(),
                    prevMessage,
                    nextState
            );
            ChatResponse response = chatModel.call(prompt);

            String finalAiMessage = responseProcessor.processResponse(
                    request,
                    response,
                    myRole.getSituation().name(),
                    session.getCurrentQuestionCount(),
                    nextState.getDepth()
            );

            // [6] Context 업데이트
            aiContextService.updateContext(aiContext.getId(), nextState.getDepth(), finalAiMessage);

            // [7] 결과 반환
            return AiResponse.builder()
                    .aiMessage(finalAiMessage)
                    .messageType(nextState.getMessageType())
                    .build();

        }, AiErrorCode.AI_SERVER_ERROR);
    }
}