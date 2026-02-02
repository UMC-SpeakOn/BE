package com.example.speakOn.global.ai.service;


import com.example.speakOn.global.ai.dto.PromptVariables;
import com.example.speakOn.global.ai.util.PromptLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptMapper {

    private final PromptLoader promptLoader;

    // 환경 변수 주입 (application.yml의 spring.ai.prompt.system 값 사용)
    @Value("${spring.ai.prompt.system.speak}")
    private String systemPromptPath;

    @Value("${AI_PROMPT_ANALYSIS_PATH}")
    private String analysisPath;


    public String mapPrompt(PromptVariables vars) throws Exception {

        // 1. 환경 변수에서 경로를 가져와 로드
        String template = promptLoader.loadYamlAsText(systemPromptPath);

        // 2. 템플릿 변수 치환
        return template
                .replace("{{name}}", vars.getName())
                .replace("{{job}}", vars.getJob())
                .replace("{{situation}}", vars.getSituation())
                .replace("{{locale}}", vars.getLocale())
                .replace("{{nationality}}", vars.getNationality())
                .replace("{{gender}}", vars.getGender())
                .replace("{{speechStyle}}", vars.getSpeechStyle());
    }


    /**
     * 분석 전용 완성형 시스템 프롬프트 반환
     */
    public String getAnalysisPrompt() throws Exception {
        return promptLoader.loadYamlAsText(analysisPath);
    }
}

