package com.example.speakOn.domain.auth.controller;

import com.example.speakOn.domain.auth.dto.AuthResponse;
import com.example.speakOn.domain.auth.dto.GoogleDTO;
import com.example.speakOn.domain.auth.dto.KakaoDTO;
import com.example.speakOn.domain.auth.service.AuthService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "로그인 및 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "카카오 인가 코드로 로그인",
            description = "카카오 로그인 후 리다이렉트된 URL의 code와 리타이렉트 URI를 보내면 백엔드가 토큰 발급부터 로그인까지 모두 처리합니다. "
    )
    @PostMapping("/kakao")
    public ApiResponse<AuthResponse.SocialLoginResponseDTO> loginWithKakaoCode(
            @Valid @RequestBody KakaoDTO.KaKaoCodeRequest request
    ) {
        AuthResponse.SocialLoginResponseDTO response = authService.loginWithKakaoCode(request.code(), request.redirectUri());
        return ApiResponse.onSuccess(response);
    }

    @Operation(
            summary = "구글 인가 코드로 로그인",
            description = "구글 로그인 후 리다이렉트된 URL의 code 파라미터만 전달하면 백엔드가 토큰 발급부터 로그인까지 모두 처리합니다. "
    )
    @PostMapping("/google")
    public ApiResponse<AuthResponse.SocialLoginResponseDTO> loginWithGoogleCode(
            @Valid @RequestBody GoogleDTO.GoogleCodeRequest request
    ) {
        AuthResponse.SocialLoginResponseDTO response = authService.loginWithGoogleCode(request.code(), request.redirectUri());
        return ApiResponse.onSuccess(response);
    }
}
