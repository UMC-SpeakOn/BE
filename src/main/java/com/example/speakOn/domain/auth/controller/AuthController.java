package com.example.speakOn.domain.auth.controller;

import com.example.speakOn.domain.auth.dto.AuthResponse;
import com.example.speakOn.domain.auth.dto.KakaoDTO;
import com.example.speakOn.domain.auth.service.AuthService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "로그인 및 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "카카오 인가 코드로 로그인",
            description = "카카오 로그인 후 리다이렉트된 URL의 code 파라미터만 전달하면 백엔드가 토큰 발급부터 로그인까지 모두 처리합니다. " +
                         "(개발시) \n" +
                    "https://kauth.kakao.com/oauth/authorize?client_id=563c6dcaaa81ce1c82cafe603d6f927f&redirect_uri=http://localhost:8080/login/oauth2/code/kakao&response_type=code" +
                            " 로 접속하여 인가 코드를 발급받아 요청 바디에 전달해주세요."

    )
    @PostMapping("/kakao")
    public ApiResponse<AuthResponse.SocialLoginResponseDTO> loginWithKakaoCode(
            @Valid @RequestBody KakaoDTO.CodeRequest request
    ) {
        AuthResponse.SocialLoginResponseDTO response = authService.loginWithKakaoCode(request.code());
        return ApiResponse.onSuccess(response);
    }
}
