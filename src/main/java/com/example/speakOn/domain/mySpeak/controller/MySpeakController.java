package com.example.speakOn.domain.mySpeak.controller;

import com.example.speakOn.domain.mySpeak.docs.MySpeakControllerDocs;
import com.example.speakOn.domain.mySpeak.dto.request.*;

import com.example.speakOn.domain.mySpeak.dto.response.*;

import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.service.MySpeakService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myspeak")
@Slf4j
public class MySpeakController implements MySpeakControllerDocs {

    private final MySpeakService mySpeakService;
    private final AuthUtil authUtil;

    //대기 화면 조회 api
    @GetMapping
    public ApiResponse<WaitScreenResponse> getWaitScreen() {
        Long userId = authUtil.getCurrentUserId();
        WaitScreenResponse response = mySpeakService.getWaitScreenForm(userId);
        return ApiResponse.onSuccess(response);
    }

    // 세션 생성 api
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<Long>> createSession(
            @Valid @RequestBody CreateSessionRequest request) {

        Long sessionId = mySpeakService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(sessionId));
    }

    //오프닝 멘트 api
    @GetMapping("/sessions/{sessionId}/opener")
    public ApiResponse<OpeningResponse> getOpener(@PathVariable Long sessionId) {
        OpeningResponse response = mySpeakService.opening(sessionId);

        return ApiResponse.onSuccess(response);
    }

    // 세션 종료 api
    @PostMapping("/sessions/{sessionId}/complete")
    public ApiResponse<CompleteSessionResponse> completeSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody CompleteSessionRequest request) {

        CompleteSessionResponse response = mySpeakService.completeSession(sessionId, request);

        return ApiResponse.onSuccess(response);
    }

    //사용자 난이도 저장 api
    @PostMapping("/sessions/{sessionId}/difficulty")
    public ApiResponse<Void> saveUserDifficulty(
            @PathVariable Long sessionId,
            @Valid @RequestBody UserDifficultyRequest request) {

        mySpeakService.saveUserDifficulty(sessionId, request);
        return ApiResponse.onSuccess(null);
    }

    //대화 한턴 보장하는 api
    @PostMapping(value = "/sessions/{sessionId}/turns", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ConversationTurnResponse> handleTurn(
            @PathVariable Long sessionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "en-US") String languageCode,
            @RequestParam(defaultValue = "MAIN") MessageType messageType
    ) {
        ConversationTurnRequest request = new ConversationTurnRequest(languageCode, messageType);
        ConversationTurnResponse response = mySpeakService.handleTurn(file, sessionId, request);
        return ApiResponse.onSuccess(response);
    }

    //대화 한턴 보장하는 api text
    @PostMapping(value = "/sessions/{sessionId}/turns/text")
    public ApiResponse<ConversationTurnTextResponse> handleTurnText(
            @PathVariable Long sessionId,
            @Valid @RequestBody ConversationTurnTextRequest request) {
        ConversationTurnTextResponse response = mySpeakService.handleTurnText(sessionId, request);
        return ApiResponse.onSuccess(response);
    }
}
