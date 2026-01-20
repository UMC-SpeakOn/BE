package com.example.speakOn.domain.mySpeak.docs;

import com.example.speakOn.domain.mySpeak.dto.request.CreateSessionRequest;
import com.example.speakOn.domain.mySpeak.dto.response.WaitScreenResponse;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

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
}
