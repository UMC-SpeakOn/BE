package com.example.speakOn.domain.mySpeak.docs;

import com.example.speakOn.domain.mySpeak.dto.request.*;
import com.example.speakOn.domain.mySpeak.dto.response.*;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.exception.code.MySpeakErrorCode;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExample;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExamples;
import com.example.speakOn.global.validation.annotation.ApiSuccessCodeExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "MySpeak", description = "MySpeak 관련 API")
public interface MySpeakControllerDocs {

    @Operation(
            summary = "대기화면 조회",
            description = """
                    사용자의 MyRole(직무, 상황, AI 정보) 목록을 조회합니다.
                    """
    )
    @ApiSuccessCodeExample(resultClass = WaitScreenResponse.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "INVALID_USER_ID"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "NO_MYROLES_AVAILABLE"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "MYROLE_FETCH_FAILED"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "MYROLE_CONVERSION_FAILED"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "WAIT_SCREEN_LOAD_FAILED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<WaitScreenResponse> getWaitScreen();


    @Operation(
            summary = "대화 세션 생성",
            description = """
                    대기화면에서 '대화 시작하기' 버튼 클릭 시 새로운 대화 세션을 생성합니다.
                    """
    )
    @ApiSuccessCodeExample(resultClass = Long.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "NO_MYROLES_AVAILABLE"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "SESSION_CREATION_FAILED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ResponseEntity<ApiResponse<Long>> createSession(@Valid @RequestBody CreateSessionRequest request);


    @Operation(
            summary = "세션 종료 처리",
            description = """
                    대화 세션을 종료하고 마무리 TTS를 생성합니다.
                    """
    )
    @ApiSuccessCodeExample(resultClass = CompleteSessionResponse.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "SESSION_NOT_FOUND"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "TTS_SYNTHESIS_FAILED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<CompleteSessionResponse> completeSession(
            @PathVariable Long sessionId,
            @RequestBody CompleteSessionRequest request
    );


    @Operation(
            summary = "세션 사용자 난이도 평가 저장",
            description = """
                    세션 완료 후 사용자 난이도 평가를 저장합니다.
                    """
    )
    @ApiSuccessCodeExample(resultClass = Void.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "SESSION_NOT_FOUND"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<Void> saveUserDifficulty(
            @PathVariable Long sessionId,
            @Valid @RequestBody UserDifficultyRequest request
    );


    @Operation(
            summary = "대화 한 턴 처리",
            description = """
                    사용자가 녹음한 음성 파일을 입력으로 받아 한 턴의 대화를 처리합니다.
                    """
    )
    @ApiSuccessCodeExample(resultClass = ConversationTurnResponse.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "INVALID_AUDIO_FORMAT"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "SESSION_NOT_FOUND"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "STT_RECOGNITION_FAILED"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "TTS_SYNTHESIS_FAILED"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "S3_UPLOAD_FAILED"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "S3_DOWNLOAD_FAILED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<ConversationTurnResponse> handleTurn(
            @PathVariable Long sessionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "en-US") String languageCode,
            @RequestParam(defaultValue = "MAIN") MessageType messageType
    );


    @Operation(
            summary = "대화 한 턴 처리 (텍스트 입력)",
            description = """
                    STT 없이 텍스트 입력만으로 한 턴의 대화를 처리합니다.
                    """
    )
    @ApiSuccessCodeExample(resultClass = ConversationTurnTextResponse.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_BAD_REQUEST"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "SESSION_NOT_FOUND"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "TTS_SYNTHESIS_FAILED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<ConversationTurnTextResponse> handleTurnText(Long sessionId, ConversationTurnTextRequest request);


    @Operation(
            summary = "AI 오프닝 멘트 조회",
            description = """
                    선택한 대화 세션에서 역할에 맞는 AI의 첫 인사를 조회합니다.
                    """
    )
    @Parameters({
            @Parameter(name = "sessionId", description = "대화 세션 ID", example = "1", required = true)
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "오프닝 멘트 생성 성공"
            )
    })
    @ApiSuccessCodeExample(resultClass = OpeningResponse.class)
    @ApiErrorCodeExamples({
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_UNAUTHORIZED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_FORBIDDEN"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "SESSION_NOT_FOUND"),
            @ApiErrorCodeExample(value = MySpeakErrorCode.class, name = "TTS_SYNTHESIS_FAILED"),
            @ApiErrorCodeExample(value = ErrorStatus.class, name = "_INTERNAL_SERVER_ERROR")
    })
    ApiResponse<OpeningResponse> getOpener(@PathVariable Long sessionId);
}
