package com.example.speakOn.global.ai.component;

import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.avatar.entity.Style;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.global.ai.dto.ConversationState;
import com.example.speakOn.global.ai.dto.PromptVariables;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.service.PromptMapper;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiPromptComponent {

    private final PromptMapper promptMapper;

    public Prompt createPrompt(MyRole myRole, Avatar avatar, Style style, String userMessage, String previousAiMessage, ConversationState nextState) {
        try {
            // 1. 변수 빌딩
            PromptVariables vars = PromptVariables.builder()
                    .name(avatar.getName())
                    .job(myRole.getJob().name())
                    .situation(myRole.getSituation().name())
                    .nationality(avatar.getNationality())
                    .locale(avatar.getLocale())
                    .gender(avatar.getGender().name())
                    .speechStyle(style.getSpeechType().name())
                    .build();

            // 2. 시스템 메시지 생성
            String systemText = promptMapper.mapPrompt(vars) + "\n\n### CURRENT INSTRUCTION ###\n" + nextState.getInstruction();

            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemText));

            // 3. 대화 맥락 추가
            if (previousAiMessage != null && !previousAiMessage.isBlank()) {
                messages.add(new AssistantMessage(previousAiMessage));
            }
            messages.add(new UserMessage(userMessage));

            return new Prompt(messages);

        } catch (Exception e) {
            log.error("[Prompt Creation Failed] roleId: {}", myRole.getId(), e);
            throw new GeneralException(AiErrorCode.AI_PARSE_ERROR);
        }
    }
}