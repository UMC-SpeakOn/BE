package com.example.speakOn.global.ai.service;
import com.example.speakOn.domain.avatar.entity.Avatar;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.domain.avatar.repository.StyleRepository;
import com.example.speakOn.domain.myRole.repository.MyRoleRepository;
import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.dto.*;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.fallback.AiFallbackHandler;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ReviewEngine;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.ReviewState;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final ChatModel chatModel;
    private final PromptMapper promptMapper; // YAML 데이터를 읽어오는 역할

    public String getAnalysisResult(String transcript) {
        return ServiceExecutor.executeSafe(() -> {

            // 1. YAML에서 여러 섹션의 프롬프트를 합쳐서 시스템 메시지 생성
            String systemInstruction = promptMapper.getAnalysisPrompt();

            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(systemInstruction),
                    new UserMessage("Transcript to analyze:\n" + transcript)
            ));

            ChatResponse response = chatModel.call(prompt);
            return response.getResult().getOutput().getText();

        }, AiErrorCode.AI_SERVER_ERROR);
    }
}
