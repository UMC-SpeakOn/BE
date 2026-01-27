package com.example.speakOn.global.ai.util;

import com.example.speakOn.global.ai.dto.ScenarioMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * AI 프롬프트 및 대화 시나리오 YAML 파일을 로드하는 유틸리티 클래스
 * - 시스템 프롬프트 템플릿 로드
 * - 상황별 대화 시나리오 매핑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromptLoader {

    /** YAML 파싱을 위한 ObjectMapper */
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * 시스템 프롬프트 템플릿을 텍스트 형태로 로드합니다.
     * (페르소나, 역할 정의 등 시스템 메시지 생성용)
     */
    public String loadYamlAsText(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            return reader.lines()
                    // UTF-8 BOM 문자 제거 (환경별 인코딩 이슈 방지)
                    .map(line -> line.replace("\uFEFF", ""))
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * 상황(Situation)에 해당하는 대화 시나리오 YAML 파일을 로드하여
     * ScenarioMapper 객체로 매핑합니다.
     */
    public ScenarioMapper loadScenario(String situation) throws IOException {

        // 상황별 시나리오 파일 경로 구성
        String path = "ai-prompts/scenarios/" + situation.toLowerCase() + ".yml";
        ClassPathResource resource = new ClassPathResource(path);

        // 시나리오 파일 존재 여부 확인
        if (!resource.exists()) {
            log.error("[PromptLoader] 시나리오 파일을 찾을 수 없습니다: {}", path);
            throw new IOException("Scenario file not found: " + path);
        }

        try {
            // YAML 파일을 ScenarioMapper로 바로 매핑하여 반환
            return yamlMapper.readValue(resource.getInputStream(), ScenarioMapper.class);
        } catch (Exception e) {
            log.error("[PromptLoader] YAML 파싱 에러 ({}): {}", situation, e.getMessage());
            throw e;
        }
    }
}
