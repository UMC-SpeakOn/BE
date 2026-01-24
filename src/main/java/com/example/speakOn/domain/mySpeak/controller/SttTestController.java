package com.example.speakOn.domain.mySpeak.controller;

import com.example.speakOn.domain.mySpeak.dto.request.SttRequestDto;
import com.example.speakOn.domain.mySpeak.dto.response.SttResponseDto;
import com.example.speakOn.domain.mySpeak.enums.MessageType;
import com.example.speakOn.domain.mySpeak.service.MySpeakService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class SttTestController {
    //삭제 할 컨트롤러 테스트 하기 위한 용도 백엔드 개발자들 용
    private final MySpeakService mySpeakService;

    @PostMapping(value = "/stt-simple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "🔥 STT 테스트 - 파일만 업로드")
    public ApiResponse<SttResponseDto> sttSimpleTest(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "ko-KR") String languageCode,
            @RequestParam(defaultValue = "MAIN") MessageType messageType,
            @RequestParam(defaultValue = "1") Long sessionId
    ) {
        log.info("🔥 STT 테스트 시작 - 파일: {}, 언어: {}",
                file.getOriginalFilename(),
                languageCode);

        SttRequestDto request = new SttRequestDto(languageCode, messageType, sessionId);
        SttResponseDto result = mySpeakService.recognizeSpeech(file, request);

        log.info("✅ STT 테스트 완료");
        return ApiResponse.onSuccess(result);
    }
}

