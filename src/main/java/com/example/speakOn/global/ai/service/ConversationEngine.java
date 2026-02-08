package com.example.speakOn.global.ai.service;

import com.example.speakOn.global.ai.dto.ScenarioMapper;
import com.example.speakOn.global.ai.util.PromptLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationEngine {

    private final PromptLoader promptLoader;
    private final Random random = new Random();

    // 축약형 정규화 맵 (간단한 NLP 전처리)
    private static final Map<String, String> CONTRACTIONS = Map.of(
            "that's", "that is",
            "i'm", "i am",
            "we're", "we are",
            "let's", "let us",
            "don't", "do not", // 부정어 체크용
            "can't", "cannot"
    );

    /**
     * 사용자 종료 의도 파악 (Java-based 1차 필터링)
     * - 축약형 풀기 (Normalization)
     * - 단어 경계(Word Boundary) 매칭
     */
    public boolean isExitSignal(String situation, String userMsg) throws Exception {
        if (userMsg == null || userMsg.isBlank()) return false;

        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        List<String> signals = scenario.getExitSignals();

        // 실제로 어떤 신호들이 로드되었는지 확인
        log.debug("[ExitCheck] Loaded signals for {}: {}", situation, signals);

        if (signals == null || signals.isEmpty()) return false;

        String normalizedMsg = normalizeText(userMsg);

        for (String signal : signals) {
            String normalizedSignal = normalizeText(signal);

            // 단어 경계(\b)를 체크하여 문장 내 어디든 정확히 매칭
            String regex = "\\b" + Pattern.quote(normalizedSignal) + "\\b";
            if (Pattern.compile(regex).matcher(normalizedMsg).find()) {

                // 부정어 체크
                if (isNegated(normalizedMsg, normalizedSignal)) {
                    log.info("[ExitCheck] Match found but negated: {}", normalizedSignal);
                    continue;
                }

                log.info("[ExitCheck] Exit signal detected: '{}' in '{}'", normalizedSignal, normalizedMsg);
                return true;
            }
        }
        return false;
    }

    /**
     * 텍스트 정규화: 소문자 변환 + 축약형 해제 + 영숫자/공백 외 제거
     */
    private String normalizeText(String text) {
        String lower = text.toLowerCase(Locale.ROOT);

        // 축약형 해제 (that's -> that is)
        for (Map.Entry<String, String> entry : CONTRACTIONS.entrySet()) {
            lower = lower.replace(entry.getKey(), entry.getValue());
        }

        // 특수문자 제거 (알파벳, 숫자, 공백만 남김)
        return lower.replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    /**
     * 부정어 체크 (Keyword 바로 앞에 'not', 'no', 'never' 등이 있는지)
     */
    private boolean isNegated(String fullText, String keyword) {
        int index = fullText.indexOf(keyword);
        if (index <= 0) return false;

        // 키워드 앞 15자 정도만 잘라서 확인
        String precedingText = fullText.substring(Math.max(0, index - 15), index).trim();
        return precedingText.endsWith("not") || precedingText.endsWith("no") || precedingText.endsWith("never") || precedingText.endsWith("don t");
    }

    public String determineNextInstruction(String situation, String userMsg, int qCount, int depth, Long seed) throws Exception {
        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        return switch (depth) {
            case 1 -> getNextMainInstruction(scenario, qCount, seed);
            case 2 -> getSmartFollowUpInstruction(scenario, "expansion", "structuring", "The user has answered. Ask for more details.");
            case 3 -> getSmartFollowUpInstruction(scenario, "reasoning", "perspective", "Ask a deeper question about their reasoning.");
            default -> "Instruction: Respond naturally to the user.";
        };
    }

    private String getNextMainInstruction(ScenarioMapper scenario, int qCount, Long seed) {
        List<String> questions = scenario.getMainQuestions();

        if (questions == null || questions.isEmpty()) {
            return "Instruction: No main questions available. Wrap up the conversation nicely. Chat end.";
        }
        if (qCount >= questions.size()) {
            return "Instruction: All planned questions are finished. Wrap up the conversation. Chat end.";
        }

        List<String> shuffledQuestions = new ArrayList<>(questions);
        long safeSeed = (seed != null) ? seed : System.currentTimeMillis();
        Collections.shuffle(shuffledQuestions, new Random(safeSeed));

        String selectedQuestion = shuffledQuestions.get(qCount);
        return "Instruction: Ask this specific main question clearly: \"" + selectedQuestion + "\"";
    }

    public String getOpener(String situation) throws Exception {
        ScenarioMapper scenario = promptLoader.loadScenario(situation);
        return getRandom(scenario.getOpeners());
    }

    private String getSmartFollowUpInstruction(ScenarioMapper scenario, String type1, String type2, String directive) {
        Map<String, List<String>> followupMap = scenario.getFollowupQuestions();
        List<String> list1 = (followupMap != null) ? followupMap.getOrDefault(type1, Collections.emptyList()) : Collections.emptyList();
        List<String> list2 = (followupMap != null) ? followupMap.getOrDefault(type2, Collections.emptyList()) : Collections.emptyList();
        String examples = makeExampleString(list1, list2);
        return "Instruction: " + directive + "\nReference styles:\n" + examples;
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