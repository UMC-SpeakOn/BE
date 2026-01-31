package com.example.speakOn.global.ai.service;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.entity.Style;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.global.ai.component.AiDataReader;
import com.example.speakOn.global.ai.component.AiPromptComponent;
import com.example.speakOn.global.ai.component.AiResponseProcessor;
import com.example.speakOn.global.ai.component.AiStateComponent;
import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.dto.AiResponse;
import com.example.speakOn.global.ai.dto.ConversationState;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.util.ServiceExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiSpeakServiceImpl implements AiSpeakService {

    private final ChatModel chatModel;

    // [Components] 역할별 분리된 컴포넌트들
    private final AiDataReader dataReader;         // 1. 데이터 읽기
    private final AiStateComponent stateComponent; // 2. 질문 상태(순서) 계산
    private final AiPromptComponent promptComponent; // 3. 프롬프트 조립
    private final AiResponseProcessor responseProcessor; // 4. 응답 처리(추출+검토+폴백)

    @Override
    public String getOpener(Long myRoleId) {
        return ServiceExecutor.executeSafe(() -> {
            MyRole myRole = dataReader.getMyRoleOrThrow(myRoleId);

            String personalGreeting = dataReader.getPersonalGreeting(myRole);
            String scenarioQuestion = stateComponent.safeGetEngineOpener(myRole.getSituation().name());

            if (personalGreeting.isBlank()) return scenarioQuestion;

            String formattedGreeting = personalGreeting.trim();
            if (!formattedGreeting.endsWith(".") && !formattedGreeting.endsWith("!")) {
                formattedGreeting += ".";
            }
            return formattedGreeting + " " + scenarioQuestion;

        }, AiErrorCode.AI_SERVER_ERROR);
    }

    @Override
    public AiResponse chat(AiRequest request) {
        return ServiceExecutor.executeSafe(() -> {
            // [1] 데이터 조회
            MyRole myRole = dataReader.getMyRoleOrThrow(request.getMyRoleId());
            Avatar avatar = myRole.getAvatar();
            Style style = dataReader.getStyleOrThrow(avatar, myRole.getSituation());

            // [2] 상태 계산
            ConversationState nextState = stateComponent.calculateNextState(
                    myRole, request.getUserMessage(),
                    request.getMainCount(), request.getDepth(), request.getSessionId()
            );

            // [3] 프롬프트 생성
            Prompt prompt = promptComponent.createPrompt(
                    myRole, avatar, style,
                    request.getUserMessage(),
                    request.getPreviousAiMessage(),
                    nextState
            );

            // [4] AI 모델 호출
            ChatResponse response = chatModel.call(prompt);

            // [5] 응답 처리 (추출 -> 리뷰 -> 폴백 통합 처리)
            String finalAiMessage = responseProcessor.processResponse(request, response, myRole.getSituation().name());

            // [6] 결과 반환
            return AiResponse.builder()
                    .aiMessage(finalAiMessage)
                    .mainCount(nextState.getMainCount())
                    .depth(nextState.getDepth())
                    .isFinished(nextState.getIsFinished())
                    .build();

        }, AiErrorCode.AI_SERVER_ERROR);
    }
}