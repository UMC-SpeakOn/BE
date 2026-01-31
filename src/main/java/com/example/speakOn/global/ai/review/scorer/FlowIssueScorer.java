package com.example.speakOn.global.ai.review.scorer;

import com.example.speakOn.global.ai.domain.ChatRequest;
import com.example.speakOn.global.ai.fallback.policy.ChatContext;
import com.example.speakOn.global.ai.review.ScenarioType;
import com.example.speakOn.global.ai.review.model.FailureType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlowIssueScorer implements IssueScorer {

    private static final List<String> QUESTION_CUES = List.of(
            "could you", "can you", "would you", "tell me", "walk me through",
            "what ", "why ", "how "
    );

    @Override
    public IssueScore score(ChatContext context, ScenarioType scenario) {
        String ai = safe(context.originalText());
        ChatRequest req = context.chatReq();
        if (ai.isBlank()) return new IssueScore(FailureType.NONE, 0.0, "ok");

        String norm = normalize(ai);
        boolean hasQuestion = ai.contains("?") || containsAny(norm, QUESTION_CUES);

        if (!hasQuestion) {
            int wc = wordCount(ai);
            double base = (scenario == ScenarioType.ONE_ON_ONE_MEETING) ? 0.55 : 0.62;
            if (wc <= 12) base += 0.08; // 짧고 질문 없으면 더 강하게
            return new IssueScore(FailureType.FLOW_ISSUE, Math.min(1.0, base), "no next question / prompt");
        }

        if (req != null && req.isFirstQuestion() && scenario == ScenarioType.INTERVIEW) {
            boolean hasIntro = norm.contains("introduce yourself") || norm.contains("tell me about yourself");
            if (!hasIntro) {
                return new IssueScore(FailureType.FLOW_ISSUE, 0.62, "missing standard interview opener");
            }
        }

        return new IssueScore(FailureType.NONE, 0.0, "ok");
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
    private static String normalize(String s) { return safe(s).toLowerCase().replaceAll("\\s+", " "); }

    private static boolean containsAny(String text, List<String> needles) {
        for (String n : needles) if (text.contains(n)) return true;
        return false;
    }

    private static int wordCount(String s) {
        String t = normalize(s).replaceAll("[^a-z0-9'\\s]", " ");
        if (t.isBlank()) return 0;
        return t.trim().split("\\s+").length;
    }
}