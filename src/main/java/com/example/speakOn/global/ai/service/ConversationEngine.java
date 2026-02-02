package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.dto.ScenarioMapper;
import com.example.speakOn.global.ai.util.PromptLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationEngine {

    private final PromptLoader promptLoader;
    private final Random random = new Random();

    /**
     * 다음 지시사항 결정
     * [Update] seed(sessionId)를 받아 중복 없는 랜덤 질문 생성
     */
    public String determineNextInstruction(String situation, String userMsg, int qCount, int depth, Long seed) throws Exception {
        ScenarioMapper scenario = promptLoader.loadScenario(situation);

        return switch (depth) {
            // Depth 1: 메인 질문 (시드 기반 셔플 적용)
            case 1 -> getNextMainInstruction(scenario, qCount, seed);

            // Depth 2, 3: 꼬리 질문
            case 2 -> getSmartFollowUpInstruction(scenario, "expansion", "structuring", "The user has answered...");
            case 3 -> getSmartFollowUpInstruction(scenario, "reasoning", "perspective", "Ask a deeper question...");

            default -> "Instruction: Respond naturally to the user.";
        };
    }

    /**
     * 메인 질문 선택 로직
     * [핵심] sessionId(seed)를 사용하여 세션별 고정된 랜덤 순서를 만듦 -> 중복 방지
     */
    private String getNextMainInstruction(ScenarioMapper scenario, int qCount, Long seed) {
        List<String> questions = scenario.getMainQuestions();

        if (questions == null || questions.isEmpty()) {
            return "Instruction: No main questions defined. Wrap up. Chat end.";
        }

        if (qCount >= questions.size()) {
            return "Instruction: All planned questions are finished. Wrap up. Chat end.";
        }

        // 1. 질문 리스트 복사
        List<String> shuffledQuestions = new ArrayList<>(questions);

        // 2. 시드 기반 셔플
        // seed(sessionId)가 같으면 항상 같은 순서로 섞임 -> 중복 없이 순차 접근 가능
        long safeSeed = (seed != null) ? seed : System.currentTimeMillis();
        Collections.shuffle(shuffledQuestions, new Random(safeSeed));

        // 3. 섞인 리스트에서 순서대로 가져옴
        String selectedQuestion = shuffledQuestions.get(qCount);

        return "Instruction: Ask this specific main question clearly: \"" + selectedQuestion + "\"";
    }

    public String getOpener(String situation) throws Exception {
        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        return getRandom(scenario.getOpeners());
    }

    public boolean isExitSignal(String situation, String userMsg) throws Exception {
        if (userMsg == null || userMsg.isBlank()) return false;
        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        List<String> signals = scenario.getExitSignals();
        if (signals == null) return false;
        String normalizedMsg = userMsg.trim().toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N} ]", "");
        return signals.stream()
                .map(s -> s.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N} ]", ""))
                .anyMatch(normalizedMsg::contains);
    }

    private String getSmartFollowUpInstruction(ScenarioMapper scenario, String type1, String type2, String directive) {
        Map<String, List<String>> followupMap = scenario.getFollowupQuestions();
        List<String> list1 = (followupMap != null) ? followupMap.getOrDefault(type1, Collections.emptyList()) : Collections.emptyList();
        List<String> list2 = (followupMap != null) ? followupMap.getOrDefault(type2, Collections.emptyList()) : Collections.emptyList();
        String examples = makeExampleString(list1, list2);
        return "Instruction: " + directive + "\nChoose the best style...\nReference styles:\n" + examples;
    }

    private String makeExampleString(List<String> list1, List<String> list2) {
        StringBuilder sb = new StringBuilder();
        appendRandomExamples(sb, list1, 2);
        appendRandomExamples(sb, list2, 2);
        return sb.toString();
    }

    private void appendRandomExamples(StringBuilder sb, List<String> list, int count) {
        if (list == null || list.isEmpty()) return;
        List<String> temp = new ArrayList<>(list);
        Collections.shuffle(temp, random);
        for (int i = 0; i < Math.min(count, temp.size()); i++) {
            sb.append("- ").append(temp.get(i)).append("\n");
        }
    }

    private String getRandom(List<String> list) {
        return (list != null && !list.isEmpty()) ? list.get(random.nextInt(list.size())) : "Hello.";
    }
}