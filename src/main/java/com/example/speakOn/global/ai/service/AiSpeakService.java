package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.dto.AiResponse;

public interface AiSpeakService {
    // 오프닝 멘트 가져오기
    String getOpener(Long myRoleId);

    // AI 대화 생성 (LLM 호출 + 검토 + Fallback)
    AiResponse chat(AiRequest request);
}