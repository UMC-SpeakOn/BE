package com.example.speakOn.domain.auth.service;

import com.example.speakOn.domain.auth.dto.AuthResponse;

public interface AuthService {

    AuthResponse.SocialLoginResponseDTO loginWithKakaoCode(String code, String redirectUri);

//    AuthResponse.SocialLoginResponseDTO loginWithGoogleCode(String code, String redirectUri);
}
