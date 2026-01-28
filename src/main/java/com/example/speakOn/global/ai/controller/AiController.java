package com.example.speakOn.global.ai.controller;

import com.example.speakOn.global.ai.docs.AiControllerDocs;
import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.dto.AiResponse;
import com.example.speakOn.global.ai.service.AiSpeakService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController implements AiControllerDocs { // 1. Docs 인터페이스 구현

    private final AiSpeakService aiService;

    /**
     * AI 오프닝 멘트 조회
     * 인터페이스(AiControllerDocs)에 정의된 Swagger 설정을 그대로 따릅니다.
     */
    @Override
    @GetMapping("/opener")
    public ApiResponse<String> getOpener(@RequestParam(name = "myRoleId") Long myRoleId) {
        // 서비스 로직 호출 및 성공 응답 반환
        return ApiResponse.onSuccess(aiService.getOpener(myRoleId));
    }

    /**
     * AI 상황 별 질문 생성
     * @Valid를 통해 AiRequest 내부에 설정한 검증 로직(NotNull, Min 등)을 활성화합니다.
     */
    @Override
    @PostMapping("/chat")
    public ApiResponse<AiResponse> chat(@RequestBody @Valid AiRequest request) {
        // DTO에서 데이터를 추출하여 서비스 레이어 전달
        return ApiResponse.onSuccess(
                aiService.chat(
                        request.getMyRoleId(),
                        request.getUserMessage(),
                        request.getQCount(),
                        request.getDepth()
                )
        );
    }
}