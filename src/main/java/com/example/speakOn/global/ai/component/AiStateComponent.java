package com.example.speakOn.global.ai.component;

import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.global.ai.dto.ConversationState;
import com.example.speakOn.global.ai.service.ConversationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiStateComponent {
    private final ConversationEngine conversationEngine;
    public static final String FIXED_CLOSING_MESSAGE = "Thanks for sharing your perspective. I appreciate your time.";

    public ConversationState calculateNextState(MyRole myRole, String userMessage, int currentQCount, int currentDepth, Long sessionId, MessageType userMessageType) throws Exception {
        String situation = myRole.getSituation().name();

        // [1] Java 기반 1차 필터링 (0ms)
        if (conversationEngine.isExitSignal(situation, userMessage)) {
            return new ConversationState(currentQCount, currentDepth, FIXED_CLOSING_MESSAGE, true, MessageType.CLOSING);
        }

        int nextDepth;
        int nextQCount = currentQCount;

        // [2] 유저 답변 타입을 기반으로 한 강제 상태 전이 (비동기 지연 방어)
        if (userMessageType == MessageType.OPENING) {
            // 오프닝 답변 후엔 무조건 첫 번째 메인 질문
            nextDepth = 1;
            nextQCount = 1;
        } else if (userMessageType == MessageType.MAIN) {
            // 메인 답변 후엔 무조건 첫 번째 꼬리 질문
            nextDepth = 2;
        } else if (userMessageType == MessageType.FOLLOW) {
            // 꼬리 질문 답변 후 단계별 처리
            if (currentDepth < 2) {
                log.warn("Session {}: FOLLOW 메시지이나 currentDepth={}로 비정상 상태입니다.", sessionId, currentDepth);
            }
            if (currentDepth == 2) {
                nextDepth = 3; // 두 번째 꼬리 질문으로
            } else {
                nextDepth = 1; // 꼬리 질문 종료 후 다음 주제(MAIN)로
                nextQCount = currentQCount + 1;
            }
        } else {
            // 예외 상황 방어 로직
            nextDepth = (currentDepth >= 3) ? 1 : currentDepth + 1;
            if (currentDepth >= 3) nextQCount++;
        }

        // [3] 지시사항 조회 (엔진이 YAML에서 가져옴)
        String instruction = conversationEngine.determineNextInstruction(situation, userMessage, nextQCount, nextDepth, sessionId);

        // [4] 시스템 종료 신호 체크
        if (instruction.toLowerCase().contains("finished") || instruction.toLowerCase().contains("chat end")) {
            return new ConversationState(nextQCount, nextDepth, FIXED_CLOSING_MESSAGE, true, MessageType.CLOSING);
        }

        MessageType messageType = (nextDepth == 1) ? MessageType.MAIN : MessageType.FOLLOW;
        return new ConversationState(nextQCount, nextDepth, instruction, false, messageType);
    }

    public String safeGetEngineOpener(String situationName) {
        try {
            return conversationEngine.getOpener(situationName);
        } catch (Exception e) {
            return "Hello! I'm ready to talk.";
        }
    }
}