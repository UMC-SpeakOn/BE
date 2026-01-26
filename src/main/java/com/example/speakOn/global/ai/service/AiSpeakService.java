package com.example.speakOn.global.ai.service;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.avatar.repository.StyleRepository;
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.global.ai.dto.*;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import com.example.speakOn.global.ai.util.ServiceExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSpeakService {

    private final ChatModel chatModel;
    private final MyRoleRepository myRoleRepository;
    private final StyleRepository styleRepository;
    private final PromptMapper promptMapper;
    private final ConversationEngine conversationEngine;

    public String getOpener(Long myRoleId) {
        return ServiceExecutor.executeSafe(() -> {
            MyRole myRole = getMyRoleOrThrow(myRoleId);
            return styleRepository.findByAvatarAndSituationType(myRole.getAvatar(), myRole.getSituation())
                    .map(style -> style.getOpenGreeting())
                    .orElseGet(() -> safeGetEngineOpener(myRole.getSituation().name()));
        }, AiErrorCode.AI_SERVER_ERROR);
    }

    /**
     * 실전 대화 진행
     */
    public AiResponse chat(Long myRoleId, String userMessage, Integer currentQCount, Integer currentDepth) {
        return ServiceExecutor.executeSafe(() -> {
            MyRole myRole = getMyRoleOrThrow(myRoleId);

            // 1. 다음 대화 상태 계산
            ConversationState nextState = calculateNextState(myRole, userMessage, currentQCount, currentDepth);

            // 2. 프롬프트 생성
            Prompt prompt = createPrompt(myRole, userMessage, nextState);

            // 3. AI 응답 생성
            String aiResponse = chatModel.call(prompt).getResult().getOutput().getText();

            return AiResponse.builder()
                    .aiMessage(aiResponse)
                    .qCount(nextState.getQCount())
                    .depth(nextState.getDepth())
                    .isFinished(nextState.isFinished())
                    .build();
        }, AiErrorCode.AI_SERVER_ERROR);
    }

    private MyRole getMyRoleOrThrow(Long myRoleId) {
        return myRoleRepository.findById(myRoleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found ID: " + myRoleId));
    }

    /**
     * 상태 전이 로직 (State Machine)
     */
    private ConversationState calculateNextState(MyRole myRole, String userMessage, int currentQCount, int currentDepth) throws Exception {
        String situation = myRole.getSituation().name();

        // 1. 종료 신호 감지
        if (conversationEngine.isExitSignal(situation, userMessage)) {
            return new ConversationState(currentQCount, currentDepth, "User wants to end. Say goodbye.", true);
        }

        // 2. 단계 전이: Opener(0) -> Main(1) -> Tail1(2) -> Tail2(3) -> Main()...
        int nextQCount = currentQCount;
        int nextDepth;

        if (currentDepth == 0) {
            nextDepth = 1; // 오프닝 답변 받았으니 첫 번째 메인 질문 시작
        } else if (currentDepth == 1) {
            nextDepth = 2; // 메인 질문 답변 받았으니 1차 꼬리 질문
        } else if (currentDepth == 2) {
            nextDepth = 3; // 1차 꼬리 답변 받았으니 2차 꼬리 질문
        } else {
            nextDepth = 1; // 2차 꼬리까지 끝났으니 다음 메인 질문으로 이동
            nextQCount = currentQCount + 1;
        }

        // 3. 엔진 지시사항 생성
        String instruction = conversationEngine.determineNextInstruction(situation, userMessage, nextQCount, nextDepth);
        boolean isFinished = instruction.contains("finished") || instruction.contains("Chat end");

        return new ConversationState(nextQCount, nextDepth, instruction, isFinished);
    }

    private Prompt createPrompt(MyRole myRole, String userMessage, ConversationState nextState) {
        try {
            Avatar avatar = myRole.getAvatar();
            PromptVariables vars = PromptVariables.builder()
                    .name(avatar.getName())
                    .job(myRole.getJob().name())
                    .situation(myRole.getSituation().name())
                    .nationality(avatar.getNationality())
                    .locale(avatar.getLocale())
                    .gender(avatar.getGender().name())
                    .cadence(avatar.getCadenceType().name())
                    .openGreeting("")
                    .build();

            String personaPrompt = promptMapper.mapPrompt(vars);

            String systemText = personaPrompt
                    + "\n\n### CURRENT INSTRUCTION ###\n"
                    + nextState.getInstruction()
                    + "\n\n### RESPONSE RULES ###\n"
                    + "1. Use the EXACT text provided in the ### INSTRUCTION ### without adding any extra sentences.\n"
                    + "2. Do not add your own opinions or pre-text.\n"
                    + "3. Do NOT include any JSON syntax or meta-commentary.";

            return new Prompt(List.of(new SystemMessage(systemText), new UserMessage(userMessage)));

        } catch (Exception e) {
            log.error("[Prompt Creation Failed] roleId: {}", myRole.getId(), e);
            throw new GeneralException(AiErrorCode.AI_PARSE_ERROR);
        }
    }

    private String safeGetEngineOpener(String situationName) {
        try {
            return conversationEngine.getOpener(situationName);
        } catch (Exception e) {
            log.warn("Engine opener failed, using fallback.", e);
            return "Hello! I'm ready to talk.";
        }
    }
}