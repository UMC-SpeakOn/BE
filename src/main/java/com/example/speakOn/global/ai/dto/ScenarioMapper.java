package com.example.speakOn.global.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @JsonProperty("closingMessages")
    private Map<String, List<String>> closingMessages;

    @JsonProperty("exitSignals")
    private List<String> exitSignals;
}