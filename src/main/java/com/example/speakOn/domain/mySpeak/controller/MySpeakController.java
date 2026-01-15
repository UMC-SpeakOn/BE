package com.example.speakOn.domain.mySpeak.controller;

import com.example.speakOn.domain.mySpeak.docs.MySpeakControllerDocs;
import com.example.speakOn.domain.mySpeak.dto.request.CreateSessionRequest;
import com.example.speakOn.domain.mySpeak.dto.response.WaitScreenResponse;
import com.example.speakOn.domain.mySpeak.service.MySpeakService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
