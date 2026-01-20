package com.example.speakOn.domain.mySpeak.docs;

import com.example.speakOn.domain.mySpeak.dto.response.WaitScreenResponse;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
}
