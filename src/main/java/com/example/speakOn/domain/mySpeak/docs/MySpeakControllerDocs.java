package com.example.speakOn.domain.mySpeak.docs;

import com.example.speakOn.domain.mySpeak.dto.request.CompleteSessionRequest;
import com.example.speakOn.domain.mySpeak.dto.request.CreateSessionRequest;
import com.example.speakOn.domain.mySpeak.dto.request.SttRequestDto;
import com.example.speakOn.domain.mySpeak.dto.request.TtsRequestDto;

import com.example.speakOn.domain.mySpeak.dto.response.CompleteSessionResponse;

import com.example.speakOn.domain.mySpeak.dto.response.SttResponseDto;
import com.example.speakOn.domain.mySpeak.dto.response.TtsResponseDto;
import com.example.speakOn.domain.mySpeak.dto.response.WaitScreenResponse;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "MySpeak", description = "MySpeak 관련 API")
public interface MySpeakControllerDocs {

    @Operation(
            summary = "대기화면 조회",
            description = """
        사용자의 MyRole(직무, 상황, AI 정보) 목록을 조회합니다.

        ### 📌 발생 가능한 에러

        - ❌ **400**: 유효하지 않은 사용자 ID (MS4001)
        - ❌ **404**: 이용 가능한 MyRole 없음 (MS4002)
        - ❌ **500**: 서버 오류
          - MS5001: 사용자 역할 조회 실패
          - MS5002: 역할 데이터 변환 실패
          - MS5003: 대기화면 로드 실패
        """
    )
    ApiResponse<WaitScreenResponse> getWaitScreen(Long userId);

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
    )ApiResponse<SttResponseDto> stt(
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
    ) ApiResponse<CompleteSessionResponse> completeSession(@PathVariable Long sessionId, @RequestBody CompleteSessionRequest request);
}
