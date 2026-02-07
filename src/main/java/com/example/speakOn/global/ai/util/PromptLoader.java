package com.example.speakOn.global.ai.util;

import com.example.speakOn.global.ai.dto.ScenarioMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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

    // 2. 시스템 설정 데이터 (speak_role.yml)
    private JsonNode roleConfig;

    @PostConstruct
    public void init() {
        try {
            // 서버 시작 시 두 파일을 메모리에 로드 (캐싱)
            this.commonData = loadScenarioInternal("common");
            loadRoleConfigInternal();

            log.info("[PromptLoader] common.yml & speak_role.yml loaded.");
        } catch (Exception e) {
            log.warn("[PromptLoader] YAML Load Failed.", e);
            this.commonData = new ScenarioMapper();
        }
    }

    /**
     * [PromptMapper용] YAML 파일을 텍스트로 통째로 읽기 (기존 메서드 유지)
     * - PromptMapper는 이 메서드를 통해 {{name}} 등을 치환하여 사용
     */
    public String loadYamlAsText(String path) throws IOException {
        // 경로가 없거나 리소스 경로라면 처리
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

    private void loadRoleConfigInternal() throws IOException {
        String path = "ai-prompts/speak_role.yml";
        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            this.roleConfig = yamlMapper.readTree(resource.getInputStream());
        }
    }
}