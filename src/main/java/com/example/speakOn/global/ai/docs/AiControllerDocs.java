package com.example.speakOn.global.ai.docs;

import com.example.speakOn.global.ai.dto.AiRequest;
import com.example.speakOn.global.ai.dto.AiResponse;
import com.example.speakOn.global.ai.exception.AiErrorCode; // 새로 만든 에러코드 임포트
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExample;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "AI API", description = "AI 기반 대화 생성 및 오프닝 멘트 관련 API입니다.")
public interface AiControllerDocs {

    @Operation(summary = "AI 오프닝 멘트 조회 API",
            description = "선택한 역할(MyRole)에 맞는 AI의 첫 인사를 조회합니다.")
    @Parameters({
            @Parameter(name = "myRoleId", description = "조회할 마이롤 ID", example = "1")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다.")
    })
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "ROLE404_1") // 기존 에러
    })
    ApiResponse<String> getOpener(@RequestParam Long myRoleId);

    @Operation(summary = "AI 대화 질문 생성 API",
            description = "사용자의 입력에 따라 대화의 흐름을 분석하고 다음 AI 질문을 생성합니다. " +
                    "토큰 제한이나 정책 위반 시 에러가 발생할 수 있습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다.")
    })
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = AiErrorCode.class, name = "AI_PROMPT_TOO_LONG"),
            @ApiErrorCodeExample(value = AiErrorCode.class, name = "AI_CONTENT_POLICY_VIOLATION"),
            @ApiErrorCodeExample(value = AiErrorCode.class, name = "AI_RATE_LIMIT_EXCEEDED"),
            @ApiErrorCodeExample(value = AiErrorCode.class, name = "AI_RESPONSE_TIMEOUT"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "ROLE404_1"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<AiResponse> chat(@RequestBody @Valid AiRequest request);
}