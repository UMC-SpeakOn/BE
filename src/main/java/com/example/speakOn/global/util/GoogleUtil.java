package com.example.speakOn.global.util;

import com.example.speakOn.domain.auth.dto.GoogleDTO;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
//@Component
@RequiredArgsConstructor
public class GoogleUtil {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    /**
     * 인가 코드로 액세스 토큰 받아오기
     *
     * @param code URL에서 받은 코드 (쿼리 파라미터 포함될 수 있음)
     * @param redirectUri 프론트에서 보낸 리다이렉트 URI (없으면 설정값 사용)
     */
    public String getAccessToken(String code, String redirectUri) {
        try {
            // 1. 코드 정제 (URL 디코딩 및 불필요한 파라미터 제거)
            String cleanCode = cleanAuthCode(code);

            log.info("[Google OAuth] 액세스 토큰 요청 시작");
            log.info("[Google OAuth] 원본 코드: {}", code);
            log.info("[Google OAuth] 정제된 코드: {}", cleanCode);
            log.info("[Google OAuth] Client ID: {}", clientId);
            log.info("[Google OAuth] Redirect URI: {}", redirectUri != null ? redirectUri : this.redirectUri);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("redirect_uri", redirectUri != null ? redirectUri : this.redirectUri);
            body.add("code", cleanCode);  // 정제된 코드 사용

            log.info("[Google OAuth] 요청 파라미터: grant_type=authorization_code, client_id={}, redirect_uri={}",
                    clientId, redirectUri != null ? redirectUri : this.redirectUri);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<GoogleDTO.TokenResponse> response = restTemplate.exchange(
                    "https://oauth2.googleapis.com/token",
                    HttpMethod.POST,
                    request,
                    GoogleDTO.TokenResponse.class
            );

            log.info("[Google OAuth] 액세스 토큰 발급 성공");
            return response.getBody().accessToken();

        } catch (Exception e) {
            log.error("[Google OAuth] 액세스 토큰 발급 실패 - 코드가 유효한지 확인하세요: {}", e.getMessage());
            log.error("[Google OAuth] 에러 상세: ", e);
            throw new ErrorHandler(ErrorStatus.INVALID_SOCIAL_ACCESS_TOKEN);
        }
    }

    /**
     * URL에서 받은 코드를 정제합니다
     * - URL 디코딩
     * - 불필요한 쿼리 파라미터 제거 (&scope=..., &authuser=..., etc)
     */
    private String cleanAuthCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ErrorHandler(ErrorStatus.INVALID_SOCIAL_ACCESS_TOKEN);
        }

        try {
            // 1. URL 디코딩
            String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
            log.info("[Google OAuth] URL 디코딩: {} → {}", code, decodedCode);

            // 2. 불필요한 파라미터 제거 (&scope=... 부분 제거)
            if (decodedCode.contains("&")) {
                decodedCode = decodedCode.substring(0, decodedCode.indexOf("&"));
                log.info("[Google OAuth] 불필요한 파라미터 제거 후: {}", decodedCode);
            }

            return decodedCode;

        } catch (Exception e) {
            log.error("[Google OAuth] 코드 정제 실패: {}", e.getMessage());
            throw new ErrorHandler(ErrorStatus.INVALID_SOCIAL_ACCESS_TOKEN);
        }
    }

    /**
     * 액세스 토큰으로 유저 정보 받아오기
     */
    public GoogleDTO.UserInfoResponse getUserInfo(String accessToken) {
        try {
            log.info("[Google OAuth] 사용자 정보 조회 시작");
            log.info("[Google OAuth] Access Token: {}...", accessToken.substring(0, Math.min(20, accessToken.length())));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<GoogleDTO.UserInfoResponse> response = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v2/userinfo",
                    HttpMethod.GET,
                    request,
                    GoogleDTO.UserInfoResponse.class
            );

            log.info("[Google OAuth] 사용자 정보 조회 성공 - ID: {}", response.getBody().id());
            return response.getBody();

        } catch (Exception e) {
            log.error("[Google OAuth] 사용자 정보 조회 실패: {}", e.getMessage());
            log.error("[Google OAuth] 에러 상세: ", e);
            throw new ErrorHandler(ErrorStatus.INVALID_SOCIAL_ACCESS_TOKEN);
        }
    }
}
