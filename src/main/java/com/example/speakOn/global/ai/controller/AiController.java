package com.example.speakOn.global.ai.controller;

import com.example.speakOn.global.ai.docs.AiControllerDocs;
import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.dto.AiResponse;
import com.example.speakOn.global.ai.service.AiSpeakServiceImpl;
import com.example.speakOn.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController implements AiControllerDocs {

    private final AiSpeakServiceImpl aiSpeakService;

    @Override
    @GetMapping("/opener")
    public ApiResponse<String> getOpener(@RequestParam(name = "myRoleId") Long myRoleId) {
        return ApiResponse.onSuccess(aiSpeakService.getOpener(myRoleId));
    }

    @Override
    @PostMapping("/chat")
    public ApiResponse<AiResponse> chat(@RequestBody @Valid AiRequest request) {
        return ApiResponse.onSuccess(aiSpeakService.chat(request));
    }
}