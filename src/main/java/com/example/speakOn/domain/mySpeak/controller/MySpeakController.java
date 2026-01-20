package com.example.speakOn.domain.mySpeak.controller;

import com.example.speakOn.domain.mySpeak.docs.MySpeakControllerDocs;
import com.example.speakOn.domain.mySpeak.dto.request.CreateSessionRequest;
import com.example.speakOn.domain.mySpeak.dto.request.SttRequestDto;
import com.example.speakOn.domain.mySpeak.dto.request.TtsRequestDto;
import com.example.speakOn.domain.mySpeak.dto.response.SttResponseDto;
import com.example.speakOn.domain.mySpeak.dto.response.TtsResponseDto;
import com.example.speakOn.domain.mySpeak.dto.response.WaitScreenResponse;
import com.example.speakOn.domain.mySpeak.service.MySpeakService;
import com.example.speakOn.global.apiPayload.ApiResponse;
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

    //대기 화면 조회 api
    @GetMapping("/{userId}")
    public ApiResponse<WaitScreenResponse> getWaitScreen(@PathVariable Long userId) {
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
}
