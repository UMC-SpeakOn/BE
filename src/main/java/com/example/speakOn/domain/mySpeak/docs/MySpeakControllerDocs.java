package com.example.speakOn.domain.mySpeak.docs;

import com.example.speakOn.domain.mySpeak.dto.request.*;

import com.example.speakOn.domain.mySpeak.dto.response.*;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.global.ai.exception.AiErrorCode;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExample;
import com.example.speakOn.global.validation.annotation.ApiErrorCodeExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.v3.oas.annotations.media.Content;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "MySpeak", description = "MySpeak 관련 API")
public interface MySpeakControllerDocs {

    @Operation(
            summary = "대기화면 조회",
            description = """
                    사용자의 MyRole(직무, 상황, AI 정보) 목록을 조회합니다.
                    
                    ### 📌 발생 가능한 에러
                    
                    - ❌ **401**: **인증 토큰 누락/만료** (AU4001)
                    - ❌ **403**: **권한 부족** (AU4002)
                    - ❌ **400**: 유효하지 않은 사용자 ID (MS4001)
                    - ❌ **404**: 이용 가능한 MyRole 없음 (MS4002)
                    - ❌ **500**: 서버 오류
                    - MS5001: 사용자 역할 조회 실패
                    - MS5002: 역할 데이터 변환 실패
                    - MS5003: 대기화면 로드 실패
                    """
    )
    ApiResponse<WaitScreenResponse> getWaitScreen();

    @Operation(
            summary = "대화 세션 생성",
            description = """
                    대기화면에서 **'대화 시작하기' 버튼** 클릭 시 새로운 대화 세션을 생성합니다.
                    
                    ### 📥 요청 파라미터
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `myRoleId` | `Long` | ✅ | 선택한 MyRole ID |
                    | `targetQuestionCount` | `Integer` | ✅ | 목표 질문 개수 (1 이상) |
                    | `startedAt` | `LocalDateTime` | ✅ | 대화 시작 시간 (프론트 전달) |
                    
                    ### 📤 응답
                    - **성공**: `201 Created`, `sessionId` 반환
                    - **실패**: 적절한 에러 코드 반환
                    
                    ### 📌 발생 가능한 에러
                    
                    - ❌ **400**: 잘못된 요청 데이터
                      - 유효하지 않은 `targetQuestionCount` (0 이하)
                      - `startedAt` 누락
                    - ❌ **404**: `MyRole`을 찾을 수 없습니다 (**MS4002**)
                    - ❌ **500**: 서버 오류
                      - **MS5004**: 대화 세션 생성 실패
                    """
    )
    ResponseEntity<ApiResponse<Long>> createSession(@Valid @RequestBody CreateSessionRequest request);

    @Operation(
            summary = "사용자 음성 STT 변환",
            description = """
                    사용자가 녹음한 **음성 파일을 텍스트로 변환(STT)** 합니다.
                    
                    - 변환이 성공하면:
                      - 텍스트 결과를 반환
                      - 해당 세션에 **USER 메시지로 대화 로그를 저장**합니다.
                    
                    ### 📥 요청 데이터
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `sessionId` | `Long` | ✅ | 현재 대화 세션 ID |
                    | `messageType` | `String` | ✅ | 메시지 타입(MAIN,FOLLOW,CLOSING) |
                    | `languageCode` | `String` | ❌ | 음성 언어 코드 (기본값: en-US) |
                    **대신에 음성파일은 무조건 multipart/form-data 로 보내기**
                    
                    ### 📤 응답
                    - **성공**: 변환된 텍스트 반환
                    - **실패**: 에러 코드 반환
                    
                    ### 📌 발생 가능한 에러
                    
                    - ❌ **400**
                      - 음성 데이터 누락
                      - sessionId 누락
                    - ❌ **404**
                      - 존재하지 않는 세션 ID (MS4004)
                    - ❌ **500**
                      - MS5005: STT 변환 실패
                      - MS4004: 지원하지 않는 오디오 파일 형식
                    """
    )
    ApiResponse<SttResponseDto> stt(
            @RequestPart("file") MultipartFile file,
            @RequestPart("meta") SttRequestDto request
    );


    @Operation(
            summary = "AI 텍스트 TTS 변환",
            description = """
                    AI가 생성한 **텍스트를 음성(TTS)으로 변환**합니다.
                    
                    - 변환이 성공하면:
                      - mp3 음성을 **base64 문자열 형태로 반환**
                      - 해당 세션에 **AI 메시지로 대화 로그를 저장**합니다.
                    
                    ### 📥 요청 데이터
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `sessionId` | `Long` | ✅ | 현재 대화 세션 ID |
                    | `text` | `String` | ✅ | 음성으로 변환할 텍스트 |
                    | `messageType` | `String` | ✅ | 메시지 타입(MAIN,FOLLOW,CLOSING) |
                    | `voiceName` | `String` | ❌ | 음성 모델 (기본값: en-US-Neural2-F(여자), en-US-Neural2-D(남자)) |
                    | `speakingRate` | `Double` | ❌ | 말하기 속도 (기본값: 1.0) |
                    
                    ### 📤 응답
                    - **성공**: base64 인코딩된 mp3 반환
                    - **실패**: 에러 코드 반환
                    
                    ### 📌 발생 가능한 에러
                    
                    - ❌ **400**
                      - text 누락
                      - sessionId 누락
                    - ❌ **404**
                      - 존재하지 않는 세션 ID (MS4004)
                    - ❌ **500**
                      - MS5007: 음성 합성 처리 중 오류
                    """
    )
    ApiResponse<TtsResponseDto> tts(@RequestBody TtsRequestDto request);

    @Operation(
            summary = "세션 종료 처리",
            description = """
                    대화 세션을 종료하고 **마무리 TTS를 생성**합니다.
                    
                    **15분 자동 종료, 사용자 종료 버튼, 질문 완료** 3가지 시나리오 모두 처리.
                    
                    **종료 성공 시**:
                    - 마무리 멘트 TTS를 **base64 문자열**로 즉시 반환
                    - **사용자 문장수 자동 계산** 및 세션 완료 상태 저장  
                    
                    ### 📥 요청 데이터
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `endedAt` | `LocalDateTime` | ✅ | 종료 시점 (현재 종료 시간) |
                    | `totalTime` | `Integer` | ✅ | 총 대화 시간 (초 단위, 일시정지 제외) |
                    
                    ### 📤 응답
                    - **성공**: 마무리 TTS base64 + 통계 정보 반환
                    - **실패**: 에러 코드 반환
                    
                    ### 📌 발생 가능한 에러
                    
                    - ❌ **400**
                     - **@NotNull 위반**: `endedAt` 또는 `totalTime` 누락
                    - ❌ **404**
                     - **MS4005**: 존재하지 않는 세션 ID
                    - ❌ **500** 
                     - **MS5007**: 마무리 TTS 생성 실패 (음성 합성 오류)
                    """
    )
    ApiResponse<CompleteSessionResponse> completeSession(@PathVariable Long sessionId, @RequestBody CompleteSessionRequest request);

    @Operation(
            summary = "세션 사용자 난이도 평가 저장",
            description = """
                    세션 완료 후 **사용자 난이도 평가**를 저장합니다.
                    저장이 성공하면:
                    세션의 `userDifficulty` 필드에 **평가값 저장**
                    리포트 화면에서 **별점 표시** 가능
                    
                    📥 요청 데이터
                    필드 | 타입 | 필수 | 설명
                    --- | --- | --- | ---
                    `userDifficulty` | `Integer` | ✅ | **사용자 평가 난이도 (1~5)**
                    
                    📤 응답
                    성공: **200 OK** (저장 완료)
                    실패: 에러 코드 반환
                    
                    📌 발생 가능한 에러
                    ❌ **400**
                    - `userDifficulty` **누락**
                    - `userDifficulty` **1~5 범위 초과**
                    
                    ❌ **404**
                    - **존재하지 않는 세션 ID** (MS4005)
                    
                    ❌ **500**
                    - **세션 업데이트 실패** (MS5008)
                    
                    """
    )
    public ApiResponse<Void> saveUserDifficulty(@PathVariable Long sessionId, @Valid @RequestBody UserDifficultyRequest request);


    @Operation(
            summary = "대화 한 턴 처리",
            description = """
                    사용자가 녹음한 **음성 파일을 입력으로 받아**
                    한 턴의 대화를 **원자적으로 처리**합니다.
                    
                    처리 흐름:
                    1. 사용자 음성 STT 변환
                    2. USER 메시지 저장
                    3. AI 질문 생성
                    4. AI 질문을 TTS로 변환
                    5. AI 메시지 저장
                    6. AI 메시지 저장 후 음성 응답 반환
                    
                    ### 📥 요청 데이터 (multipart/form-data)
                    
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `file` | File | ✅ | 사용자 음성 파일 |
                    | `request` | Object | ✅ | 대화 턴 메타 정보 |
                    | `languageCode` | String | ❌ | 음성 언어 코드 (기본값: en-US) |
                    | `messageType` | String | ❌ | 메시지 타입 (MAIN, FOLLOW, CLOSING) |
                    
                    > ⚠️ 음성 파일은 반드시 `multipart/form-data` 형식으로 전송해야 합니다.
                    > languageCode, messageType 디폴트로 en-US, MAIN 이지만 messageType은 메인 질문 아니면 꼭 보내주세요!
                    
                    ---
                    
                    ### 📤 응답 데이터
                    
                    | 필드 | 타입 | 설명 |
                    |------|------|------|
                    | `text` | String | AI가 생성한 질문 텍스트 |
                    | `base64Audio` | String | base64 인코딩된 AI 음성(mp3) |
                    | `messageType` | String | 대화 메시지 타입 |
                    
                    ---
                    
                    ### ❗ 발생 가능한 에러
                    
                    #### ❌ 400 Bad Request
                    - 음성 파일 누락
                    - request 데이터 누락 또는 형식 오류
                    - 지원하지 않는 오디오 파일 형식 (MS4004)
                    
                    #### ❌ 404 Not Found
                    - 존재하지 않는 세션 ID (MS4004)
                    
                    #### ❌ 500 Internal Server Error
                    - STT 변환 실패 (MS5005)
                    - TTS 변환 실패
                    - AI 질문 생성 실패
                    """
    )
    ApiResponse<ConversationTurnResponse> handleTurn(
            @PathVariable Long sessionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "en-US") String languageCode,  // ← @RequestParam!
            @RequestParam(defaultValue = "MAIN") MessageType messageType  // ← @RequestParam!
    );

    @Operation(
            summary = "대화 한 턴 처리 (텍스트 입력)",
            description = """
                    STT 없이 **텍스트 입력만으로**
                    한 턴의 대화를 처리합니다.
                    
                    ### 📥 요청 데이터
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `text` | String | ✅ | 사용자 입력 텍스트 |
                    | `languageCode` | String | ❌ | 언어 코드 (기본값: en-US) |
                    | `messageType` | String | ✅ | 메시지 타입 (MAIN, FOLLOW, CLOSING) |
                    
                    ### 📤 응답
                    | 필드 | 타입 | 설명 |
                    |------|------|------|
                    | `text` | String | AI 질문 텍스트 |
                    | `base64Audio` | String | base64 인코딩된 AI 음성 |
                    | `messageType` | String | AI 메시지 타입 |
                    
                    ### ❗ 에러
                    - ❌ 404: 존재하지 않는 세션 (MS4004)
                    - ❌ 500: AI 질문 생성 / TTS 실패
                    """
    )
    ApiResponse<ConversationTurnTextResponse> handleTurnText(Long sessionId, ConversationTurnTextRequest request);


    @Operation(
            summary = "AI 오프닝 멘트 조회",
            description = """
                    선택한 대화 세션에서 역할에 맞는 **AI의 첫 인사**를 조회합니다.
                    
                    ### 📥 요청
                    | 필드 | 타입 | 필수 | 설명 |
                    |------|------|------|------|
                    | `sessionId` | Long | ✅ | 대화 세션 ID |
                    
                    ### 📤 응답
                    | 필드 | 타입 | 설명 |
                    |------|------|------|
                    | `text` | String | 오프닝 멘트 텍스트 |
                    | `base64Audio` | String | Base64 인코딩된 음성 데이터 |
                    | `messageType` | String | AI 메시지 타입 |
                    
                    ### 🚀 사용 예시
                    ```
                    GET /api/myspeak/sessions/1/opener
                    ```
                    AI가 세션 역할에 맞춰 자연스러운 오프닝 멘트를 생성합니다.
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
    }) ApiResponse<OpeningResponse> getOpener(@PathVariable Long sessionId);
}
