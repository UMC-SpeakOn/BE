package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.dto.ScenarioMapper;
import com.example.speakOn.global.ai.util.PromptLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationEngine {

    private final PromptLoader promptLoader;
    private final Random random = new Random();

    /**
     * 현재 상황(situation)과 대화 단계(depth)에 따라
     * 다음 AI 지시사항(Instruction)을 결정합니다.
     */
    public String determineNextInstruction(String situation, String userMsg, int qCount, int depth) throws Exception {
        ScenarioMapper scenario = promptLoader.loadScenario(situation);

        return switch (depth) {
            // Depth 1: 메인 질문 단계
            case 1 -> getRandomMainInstruction(scenario, qCount);

            // Depth 2: 1차 꼬리 질문 단계
            case 2 -> getSmartFollowUpInstruction(
                    scenario,
                    "expansion", "structuring",
                    "The user has answered the main question. Ask a follow-up to clarify details (Expansion) OR ask about the process (Structuring)."
            );

            // Depth 3: 2차 꼬리 질문 단계
            case 3 -> getSmartFollowUpInstruction(
                    scenario,
                    "reasoning", "perspective",
                    "Ask a deeper question about their reasoning (Why) OR ask about alternative viewpoints/risks (Perspective)."
            );

            // 기타 예외 상황
            default -> "Instruction: Respond naturally to the user.";
        };
    }

    /**
     * 메인 질문 목록 중 하나를 선택하여
     * 명확한 질문 지시사항(Instruction)으로 변환합니다.
     */
    private String getRandomMainInstruction(ScenarioMapper scenario, int qCount) {
        List<String> questions = scenario.getMainQuestions();

        if (questions == null || questions.isEmpty()) {
            return "Instruction: No main questions defined. Wrap up the conversation.";
        }

        if (qCount >= questions.size()) {
            return "Instruction: All planned questions are finished. Provide a summary and gently wrap up the conversation.";
        }

        // 질문 목록을 무작위로 섞어 자연스러운 흐름 유지
        List<String> shuffledQuestions = new java.util.ArrayList<>(questions);
        Collections.shuffle(shuffledQuestions, random);

        // 질문 수를 초과하더라도 순환되도록 처리
        String selectedQuestion = shuffledQuestions.get(qCount % shuffledQuestions.size());

        return "Instruction: Ask this specific main question clearly: \"" + selectedQuestion + "\"";
    }

    /**
     * 대화 시작 시 사용할 오프너 문장 반환
     */
    public String getOpener(String situation) throws Exception {
        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        return getRandom(scenario.getOpeners());
    }

    /**
     * 사용자 발화에서 대화 종료 의도가 있는지 판단
     */
    public boolean isExitSignal(String situation, String userMsg) throws Exception {
        if (userMsg == null || userMsg.isBlank()) return false;

        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        List<String> signals = scenario.getExitSignals();
        if (signals == null) return false;

        // 사용자 입력을 정규화하여 비교 정확도 향상
        String normalizedMsg = userMsg.trim().toLowerCase().replaceAll("[^a-z ]", "");

        return signals.stream()
                .map(s -> s.toLowerCase().replaceAll("[^a-z ]", ""))
                .anyMatch(normalizedMsg::contains);
    }

    // =========================
    // Private Helper Methods
    // =========================

    /**
     * 꼬리 질문(Follow-up) 지시사항 생성
     * - 시나리오 정의가 없거나 null이어도 안전하게 처리
     * - 예시 질문은 참고용이며 그대로 복사하지 않도록 유도
     */
    private String getSmartFollowUpInstruction(
            ScenarioMapper scenario,
            String type1,
            String type2,
            String directive
    ) {
        // followupQuestions 자체가 null일 수 있으므로 방어 처리
        Map<String, List<String>> followupMap = scenario.getFollowupQuestions();

        List<String> list1 = (followupMap != null)
                ? followupMap.getOrDefault(type1, Collections.emptyList())
                : Collections.emptyList();

        List<String> list2 = (followupMap != null)
                ? followupMap.getOrDefault(type2, Collections.emptyList())
                : Collections.emptyList();

        String examples = makeExampleString(list1, list2);

        return "Instruction: " + directive + "\n" +
                "Choose the best style that fits the user's previous answer.\n" +
                "Reference styles (Adapt, do not copy): \n" +
                examples;
    }

    /**
     * 두 개의 질문 리스트에서 랜덤 예시를 조합하여 출력
     */
    private String makeExampleString(List<String> list1, List<String> list2) {
        StringBuilder sb = new StringBuilder();
        appendRandomExamples(sb, list1, 2);
        appendRandomExamples(sb, list2, 2);
        return sb.toString();
    }

    /**
     * 주어진 리스트에서 지정된 개수만큼 랜덤 예시 추가
     */
    private void appendRandomExamples(StringBuilder sb, List<String> list, int count) {
        if (list == null || list.isEmpty()) return;

        List<String> temp = new java.util.ArrayList<>(list);
        Collections.shuffle(temp, random);

        for (int i = 0; i < Math.min(count, temp.size()); i++) {
            sb.append("- ").append(temp.get(i)).append("\n");
        }
    }

    /**
     * 리스트에서 랜덤 값 하나 반환
     * 값이 없을 경우 기본 인사말 반환
     */
    private String getRandom(List<String> list) {
        return (list != null && !list.isEmpty())
                ? list.get(random.nextInt(list.size()))
                : "Hello.";
    }
}
