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

    @Value("${AI_PROMPT_ANALYSIS_PATH}")
    private String analysisPath;

    /**
     * 아바타 및 대화 컨텍스트 정보(PromptVariables)를
     * 시스템 프롬프트 템플릿에 치환하여 최종 프롬프트 문자열을 생성합니다.
     */
    public String mapPrompt(PromptVariables vars) throws Exception {

        // 1. 시스템 프롬프트 템플릿 로드 (YAML → Plain Text)
        String template = promptLoader.loadYamlAsText("ai-prompts/system/speak_role.yml");

        // 2. 템플릿 변수 치환 ({{variable}} → 실제 값)
        return template
                .replace("{{name}}", vars.getName())
                .replace("{{job}}", vars.getJob())
                .replace("{{situation}}", vars.getSituation())
                .replace("{{locale}}", vars.getLocale())
                .replace("{{cadence}}", vars.getCadence())
                .replace("{{nationality}}", vars.getNationality())
                .replace("{{gender}}", vars.getGender());
    }

    /**
     * 분석 전용 완성형 시스템 프롬프트 반환
     */
    public String getAnalysisPrompt() throws Exception {
        return promptLoader.loadYamlAsText(analysisPath);
    }
}
