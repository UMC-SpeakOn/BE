package com.example.speakOn.global.ai.service;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.ai.util.ServiceExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

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
