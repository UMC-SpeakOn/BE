package com.example.speakOn.global.ai.service;


import com.example.speakOn.domain.myRole.entity.MyRole;
import com.example.speakOn.global.ai.dto.PromptVariables;
import com.example.speakOn.global.ai.util.PromptLoader;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptMapper {

    private final PromptLoader promptLoader;

    // 환경 변수 주입 (application.yml의 spring.ai.prompt.system 값 사용)
    @Value("${spring.ai.prompt.system.speak}")
    private String systemPromptPath;

    @Value("${spring.ai.prompt.analysis}")
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
    public String getAnalysisPrompt(MyRole myRole) throws Exception {
        try {
            String template = promptLoader.loadYamlAsText(analysisPath);

            return template
                    .replace("{{job}}", myRole.getJob().name())
                    .replace("{{situation}}", myRole.getSituation().name());

        } catch (IOException e) {
            log.error("Analysis prompt load failed", e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}

