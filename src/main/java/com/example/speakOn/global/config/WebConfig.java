package com.example.speakOn.global.config;

import com.example.speakOn.global.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig {

    private final CorsProperties corsProperties;

    /**
     * CORS 설정 정보를 담은 CorsConfigurationSource Bean 등록
     *
     * 이 Bean은 Spring Security 또는 Spring MVC에서
     * HTTP 요청에 대해 CORS 정책을 적용할 때 사용됨
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin 목록 설정
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // 모든 요청 헤더 허용
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition")); // 응답 헤더
        config.setAllowCredentials(true); // 자격 증명(쿠키, 인증 헤더 등)을 포함한 요청을 허용
        config.setMaxAge(3600L); // Preflight 요청(OPTIONS)의 캐시 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 정의한 CORS 정책 적용
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
