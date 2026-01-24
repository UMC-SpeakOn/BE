package com.example.speakOn.global.ai.controller;

import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.dto.AiRequestDto;
import com.example.speakOn.global.ai.service.AiService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "AI API", description = "AI 연동 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "AI 대화 테스트", description = "시스템 역할과 사용자 메시지를 보내 AI 응답을 받습니다.")
    public ApiResponse<String> testAiChat(@RequestBody @Valid AiRequestDto request) {

        // 1. 메시지 생성
        SystemMessage systemMsg = new SystemMessage(request.getSystemMessage());
        UserMessage userMsg = new UserMessage(request.getUserMessage());

        // 1.2 도메인 ChatRequest 생성 (리뷰/폴백에서 사용)
        ChatRequest chatReq = ChatRequest.voice(
                request.getSessionId(),
                request.getTurn(),
                request.getUserText()
        );

        // 2. 프롬프트 조립 및 호출
        Prompt prompt = new Prompt(List.of(systemMsg, userMsg));
        String response = aiService.callAi(chatReq, prompt, request.getScenarioType());



        return ApiResponse.onSuccess(response);
    }


}