package com.example.speakOn.global.util;

import com.example.speakOn.domain.auth.dto.KakaoDTO;
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

@Slf4j
@Component // 스프링 빈 등록
@RequiredArgsConstructor
public class KakaoUtil {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 인가 코드로 액세스 토큰 받아오기
     */
    public String getAccessToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("redirect_uri", redirectUri);
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<KakaoDTO.TokenResponse> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    request,
                    KakaoDTO.TokenResponse.class
            );
            return response.getBody().accessToken();

        } catch (Exception e) {
            throw new com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler(
                    com.example.speakOn.global.apiPayload.code.status.ErrorStatus.INVALID_SOCIAL_ACCESS_TOKEN
            );
        }
    }

    /**
     * 액세스 토큰으로 유저 정보 받아오기
     */
    public KakaoDTO.UserInfoResponse getUserInfo(String accessToken) {
        try {
            log.info("카카오 사용자 정보 조회 시작 - AccessToken: {}...", accessToken.substring(0, Math.min(10, accessToken.length())));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<KakaoDTO.UserInfoResponse> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    request,
                    KakaoDTO.UserInfoResponse.class
            );

            log.info("카카오 사용자 정보 조회 성공 - ID: {}", response.getBody().id());
            return response.getBody();

        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler(
                    com.example.speakOn.global.apiPayload.code.status.ErrorStatus.INVALID_SOCIAL_ACCESS_TOKEN
            );
        }
    }
}