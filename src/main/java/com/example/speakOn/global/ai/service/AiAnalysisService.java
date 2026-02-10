package com.example.speakOn.global.ai.service;
import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.global.ai.component.ServiceExecutor;
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
public class AiAnalysisService {

    private final ChatModel chatModel;
    private final PromptMapper promptMapper;
    private final ServiceExecutor serviceExecutor;

    public String getAnalysisResult(String transcript, MyRole myRole) {
        return serviceExecutor.executeSafe(() -> {

            // 1. YAML에서 여러 섹션의 프롬프트를 합쳐서 시스템 메시지 생성
            String systemInstruction = promptMapper.getAnalysisPrompt(myRole);

            log.info("SENT SYSTEM PROMPT: {}", systemInstruction);

            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(systemInstruction),
                    new UserMessage("Transcript to analyze:\n" + transcript)
            ));

            ChatResponse response = chatModel.call(prompt);
            return response.getResult().getOutput().getText();

        });
    }
}
