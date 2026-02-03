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

    public ConversationState calculateNextState(MyRole myRole, String userMessage, int currentQCount, int currentDepth, Long sessionId) throws Exception {
        String situation = myRole.getSituation().name();

        // 1. 종료 시그널 확인
        if (conversationEngine.isExitSignal(situation, userMessage)) {
            return new ConversationState(currentQCount, currentDepth, "User wants to end. Say goodbye.", true, MessageType.CLOSING);
        }

        // 2. 상태 갱신 로직 (0:오프닝 -> 1:메인 -> 2:꼬리1 -> 3:꼬리2 -> 1:다음메인)
        int nextDepth;
        int nextQCount = currentQCount;

        if (currentDepth == 3) {
            nextDepth = 1;      // 꼬리2(3) -> 다음 메인(1)
            nextQCount++;       // 질문 카운트 증가
        } else {
            nextDepth = currentDepth + 1; // 0->1, 1->2, 2->3
        }

        // 3. 지시사항 조회
        String instruction = conversationEngine.determineNextInstruction(situation, userMessage, nextQCount, nextDepth, sessionId);
        boolean isFinished = instruction.toLowerCase().contains("finished");

        // 4. 메시지 타입 결정 (1: 메인, 그 외: 꼬리)
        MessageType messageType;
        if (isFinished) {
            messageType = MessageType.CLOSING;
        } else {
            messageType = (nextDepth == 1) ? MessageType.MAIN : MessageType.FOLLOW;
        }

        return new ConversationState(nextQCount, nextDepth, instruction, isFinished, messageType);
    }

    public String safeGetEngineOpener(String situationName) {
        try {
            return conversationEngine.getOpener(situationName);
        } catch (Exception e) {
            return "Hello! I'm ready to talk.";
        }
    }
}