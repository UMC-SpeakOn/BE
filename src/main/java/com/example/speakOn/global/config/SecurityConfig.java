package com.example.speakOn.global.config;

import com.example.speakOn.global.jwt.JwtAuthenticationFilter;
import com.example.speakOn.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보안 비활성화 (JWT는 세션 기반이 아니라서 불필요)
                .cors(Customizer.withDefaults()) // CORS 설정 적용
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .oauth2Login(AbstractHttpConfigurer::disable) // OAuth2 로그인 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 세션 관리 정책 설정 (STATELESS: 세션을 만들지도, 쓰지도 않음)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/css/**", "/js/**", "/images/**", "/favicon.ico",  // 정적 리소스
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", // Swagger 문서
                                "/error", "/actuator/health",
                                "/api/auth/**", // 인증 관련 API
                                "/api/ai/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 추가
                // UsernamePasswordAuthenticationFilter(기본 로그인 필터) 앞에서 실행되도록 배치
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
