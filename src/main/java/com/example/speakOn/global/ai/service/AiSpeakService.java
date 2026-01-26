package com.example.speakOn.global.ai.service;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.avatar.repository.StyleRepository;
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.global.ai.dto.*;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
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

    /**
     * 상황별 오프닝 멘트 조회
     */
    public String getOpener(Long myRoleId) {
        return ServiceExecutor.executeSafe(() -> {
            MyRole myRole = getMyRoleOrThrow(myRoleId);
            return styleRepository.findByAvatarAndSituationType(myRole.getAvatar(), myRole.getSituation())
                    .map(style -> style.getOpenGreeting())
                    .orElseGet(() -> safeGetEngineOpener(myRole.getSituation().name()));
        }, AiErrorCode.AI_SERVER_ERROR);
    }

    /**
     * 실전 대화 진행 (NPE 방어 로직 포함)
     */
    public AiResponse chat(Long myRoleId, String userMessage, Integer currentQCount, Integer currentDepth) {
        return ServiceExecutor.executeSafe(() -> {
            MyRole myRole = getMyRoleOrThrow(myRoleId);

            // 1. NPE 방어: Integer 파라미터 null 체크 및 기본값(0) 설정
            int safeQCount = (currentQCount != null) ? currentQCount : 0;
            int safeDepth = (currentDepth != null) ? currentDepth : 0;

            // 2. 다음 대화 상태 계산 (원시 타입 int 전달로 언박싱 에러 방지)
            ConversationState nextState = calculateNextState(myRole, userMessage, safeQCount, safeDepth);

            // 3. 프롬프트 생성
            Prompt prompt = createPrompt(myRole, userMessage, nextState);

            // 4. AI 응답 생성
            ChatResponse response = chatModel.call(prompt);
            String aiResponse = java.util.Optional.ofNullable(response.getResult().getOutput().getText())
                    .filter(t -> !t.isBlank())
                    .orElseThrow(() -> new GeneralException(AiErrorCode.AI_PARSE_ERROR));

            // 5. 응답 DTO 조립 (isFinished() 게터 사용)
            return AiResponse.builder()
                    .aiMessage(aiResponse)
                    .qCount(nextState.getQCount())
                    .depth(nextState.getDepth())
                    .isFinished(nextState.getIsFinished())
                    .build();
        }, AiErrorCode.AI_SERVER_ERROR);
    }

    private MyRole getMyRoleOrThrow(Long myRoleId) {
        return myRoleRepository.findById(myRoleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MY_ROLE_NOT_FOUND));
    }

    /**
     * 상태 전이 로직 (State Machine)
     */
    private ConversationState calculateNextState(MyRole myRole, String userMessage, int currentQCount, int currentDepth) throws Exception {
        String situation = myRole.getSituation().name();

        // 1. 종료 신호 감지 (엔진 활용)
        if (conversationEngine.isExitSignal(situation, userMessage)) {
            return new ConversationState(currentQCount, currentDepth, "User wants to end. Say goodbye.", true);
        }

        // 2. 단계 전이: Opener(0) -> Main(1) -> Tail1(2) -> Tail2(3) -> 차기 Main(1)...
        int nextQCount = currentQCount;
        int nextDepth;

        if (currentDepth == 0) {
            nextDepth = 1; // 오프닝 이후 첫 메인 질문
        } else if (currentDepth == 1) {
            nextDepth = 2; // 1차 꼬리 질문
        } else if (currentDepth == 2) {
            nextDepth = 3; // 2차 꼬리 질문
        } else {
            nextDepth = 1; // 로테이션: 다음 메인 질문으로 이동
            nextQCount = currentQCount + 1;
        }

        // 3. 엔진 지시사항 생성
        String instruction = conversationEngine.determineNextInstruction(situation, userMessage, nextQCount, nextDepth);

        // 지시사항 텍스트 기반 종료 판정
        boolean isFinished = instruction.contains("finished") || instruction.contains("Chat end");

        return new ConversationState(nextQCount, nextDepth, instruction, isFinished);
    }

    /**
     * AI 프롬프트 생성 (페르소나 + 지시사항 + 규칙)
     */
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

            // YAML 기반 페르소나 매핑
            String personaPrompt = promptMapper.mapPrompt(vars);

            // 시스템 메시지 구성
            String systemText = personaPrompt
                    + "\n\n### CURRENT INSTRUCTION ###\n"
                    + nextState.getInstruction()
                    + "\n\n### RESPONSE RULES ###\n"
                    + "1. Use the EXACT text provided in the ### INSTRUCTION ### without adding any extra sentences.\n"
                    + "2. Do not add your own opinions or pre-text.\n"
                    + "3. Do NOT include any JSON syntax or meta-commentary.";

            return new Prompt(List.of(
                    new SystemMessage(systemText),
                    new UserMessage(userMessage)
            ));

        } catch (Exception e) {
            log.error("[Prompt Creation Failed] roleId: {}", myRole.getId(), e);
            throw new GeneralException(AiErrorCode.AI_PARSE_ERROR);
        }
    }

    /**
     * 엔진 오프닝 실패 시 Fallback 로직
     */
    private String safeGetEngineOpener(String situationName) {
        try {
            return conversationEngine.getOpener(situationName);
        } catch (Exception e) {
            log.warn("Engine opener failed, using fallback.", e);
            return "Hello! I'm ready to talk.";
        }
    }
}