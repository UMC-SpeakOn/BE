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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myspeak")
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

    // STT api
    @PostMapping(value = "/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SttResponseDto> stt(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("meta") SttRequestDto request
            // Jwt 토큰 구현시에 userId 받아오게 수정
    ) {
        SttResponseDto result = mySpeakService.recognizeSpeech(file, request);
        return ApiResponse.onSuccess(result);
    }


    // TTS api
    @PostMapping("/tts")
    public ApiResponse<TtsResponseDto> tts(@Valid @RequestBody TtsRequestDto request) {

        byte[] audioBytes = mySpeakService.generateSpeech(request);

        String base64 = Base64.getEncoder().encodeToString(audioBytes);

        return ApiResponse.onSuccess(new TtsResponseDto(base64));
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

    @PostMapping(value = "/sessions/{sessionId}/turns",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ConversationTurnResponse> handleTurn(
            @PathVariable Long sessionId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "en-US") String languageCode,  // ← @RequestParam!
            @RequestParam(defaultValue = "MAIN") MessageType messageType  // ← @RequestParam!
    ) {
        // 서비스에서 ConversationTurnRequest 생성
        ConversationTurnRequest request = new ConversationTurnRequest(languageCode, messageType);
        ConversationTurnResponse response = mySpeakService.handelTurn(file, sessionId, request);
        return ApiResponse.onSuccess(response);
    }

}
