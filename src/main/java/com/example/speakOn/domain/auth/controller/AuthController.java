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
            description = "카카오 로그인 후 리다이렉트된 URL의 code와 리타이렉트 URI를 보내면 백엔드가 토큰 발급부터 로그인까지 모두 처리합니다. " +
                         "(개발시) \n" +
                    "https://kauth.kakao.com/oauth/authorize?client_id=563c6dcaaa81ce1c82cafe603d6f927f&redirect_uri=http://localhost:8080/login/oauth2/code/kakao&response_type=code" +
                            " 로 접속하여 인가 코드를 발급받아 요청 바디에 전달해주세요."

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
            description = "구글 로그인 후 리다이렉트된 URL의 code 파라미터만 전달하면 백엔드가 토큰 발급부터 로그인까지 모두 처리합니다. " +
                    "(개발시) 아래 url에서 인가 코드를 받아 요청 바디에 전달해주세요." +
                    "https://accounts.google.com/o/oauth2/v2/auth?client_id=170876738810-o5t93iktkb7qi76t5t3bba5n7sahb4o8.apps.googleusercontent.com&redirect_uri=http://localhost:5173/login/oauth2/code/google&response_type=code&scope=email+profile"
    )
    @PostMapping("/google")
    public ApiResponse<AuthResponse.SocialLoginResponseDTO> loginWithGoogleCode(
            @Valid @RequestBody GoogleDTO.GoogleCodeRequest request
    ) {
        AuthResponse.SocialLoginResponseDTO response = authService.loginWithGoogleCode(request.code(), request.redirectUri());
        return ApiResponse.onSuccess(response);
    }
}
