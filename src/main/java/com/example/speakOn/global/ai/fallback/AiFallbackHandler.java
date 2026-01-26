package com.example.speakOn.global.ai.fallback;

import com.example.speakOn.global.ai.exception.AiResponseValidator;
import com.example.speakOn.global.ai.exception.AiValidationResult;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
public class AiFallbackHandler {

    public String handle(
            Prompt prompt,
            ChatResponse response,
            AiValidationResult validation
    ) {
        // 임시 fallback
        return "죄송합니다. 다시 한 번 말씀해 주세요.";
    }
}