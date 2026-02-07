package com.example.speakOn.global.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioMapper {

    private String type;

    @JsonProperty("openers")
    private List<String> openers;

    @JsonProperty("mainQuestions")
    private List<String> mainQuestions;

    @JsonProperty("followupQuestions")
    private Map<String, List<String>> followupQuestions;

    @JsonProperty("exitSignals")
    private List<String> exitSignals;

    /**
     * [추가] Common 데이터 병합 메서드
     * 자신의 필드가 비어있을 때만 Common 데이터로 채웁니다.
     */
    public void mergeCommonData(ScenarioMapper common) {
        if (common == null) return;

        if (this.openers == null) this.openers = new ArrayList<>(common.openers != null ? common.openers : new ArrayList<>());
        if (this.mainQuestions == null) this.mainQuestions = new ArrayList<>(common.mainQuestions != null ? common.mainQuestions : new ArrayList<>());

        if (this.followupQuestions == null) this.followupQuestions = new HashMap<>();
        if (common.followupQuestions != null) common.followupQuestions.forEach(this.followupQuestions::putIfAbsent);

        if (this.exitSignals == null) {
            this.exitSignals = new ArrayList<>();
        }
        if (common.exitSignals != null) {
            for (String signal : common.exitSignals) {
                if (!this.exitSignals.contains(signal)) {
                    this.exitSignals.add(signal);
                }
            }
        }
    }
}