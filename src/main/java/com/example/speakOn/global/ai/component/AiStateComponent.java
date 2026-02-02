package com.example.speakOn.global.ai.component;

import com.example.speakOn.domain.myRole.entity.MyRole;
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

    /**
     * 현재 상황과 사용자 메시지를 분석하여 다음 대화 상태(Instruction, QCount, Depth)를 계산
     */
    public ConversationState calculateNextState(MyRole myRole, String userMessage, int currentQCount, int currentDepth, Long sessionId) throws Exception {
        String situation = myRole.getSituation().name();

        // 1. 종료 신호 감지
        if (conversationEngine.isExitSignal(situation, userMessage)) {
            return new ConversationState(currentQCount, currentDepth, "User wants to end. Say goodbye.", true);
        }

        // 2. 질문 카운트 및 깊이(Depth) 갱신 로직
        int nextQCount = currentQCount;
        int nextDepth = (currentDepth == 3) ? 1 : currentDepth + 1;
        if (currentDepth == 3) nextQCount++;

        // 3. 다음 지시사항(Instruction) 조회
        String instruction = conversationEngine.determineNextInstruction(situation, userMessage, nextQCount, nextDepth, sessionId);
        boolean isFinished = instruction.contains("finished") || instruction.contains("Chat end");

        return new ConversationState(nextQCount, nextDepth, instruction, isFinished);
    }

    // 엔진에서 오프닝 멘트 가져오기 (실패 시 기본값)
    public String safeGetEngineOpener(String situationName) {
        try {
            return conversationEngine.getOpener(situationName);
        } catch (Exception e) {
            log.error("Opener load failed for situation={}", situationName, e);
            return "Hello! I'm ready to talk.";
        }
    }

}