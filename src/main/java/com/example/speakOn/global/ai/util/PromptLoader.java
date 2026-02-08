package com.example.speakOn.global.ai.util;

import com.example.speakOn.global.ai.dto.ScenarioMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromptLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    // 1. 공통 시나리오 데이터 (common.yml)
    private ScenarioMapper commonData;

    @PostConstruct
    public void init() {
        try {
            this.commonData = loadScenarioInternal("common");
            log.info("[PromptLoader] common.yml loaded.");
        } catch (Exception e) {
            log.warn("[PromptLoader] common.yml load failed.", e);
            this.commonData = new ScenarioMapper();
        }
    }

    /**
     * [PromptMapper용] YAML 파일을 텍스트로 통째로 읽기 (기존 메서드 유지)
     * - PromptMapper는 이 메서드를 통해 {{name}} 등을 치환하여 사용
     */
    public String loadYamlAsText(String path) throws IOException {
        if (path == null || path.isBlank()) return "";
        if (path.startsWith("classpath:")) {
            path = path.substring(10);
        }

        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) return "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * [ConversationEngine용] 시나리오 매퍼 로드 (common 데이터 자동 병합)
     */
    public ScenarioMapper loadScenario(String situation) {
        ScenarioMapper scenario;
        try {
            scenario = loadScenarioInternal(situation);
        } catch (IOException e) {
            log.warn("[PromptLoader] Scenario '{}' load failed: {}", situation, e.getMessage());
            scenario = new ScenarioMapper();
        }
        scenario.mergeCommonData(this.commonData);
        return scenario;
    }

    private ScenarioMapper loadScenarioInternal(String filename) throws IOException {
        String path = "ai-prompts/scenarios/" + filename.toLowerCase() + ".yml";
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) throw new IOException(path + " not found");
        return yamlMapper.readValue(resource.getInputStream(), ScenarioMapper.class);
    }

}