package com.example.speakOn.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // 1. Request Header에서 토큰 정보 추출
        String token = resolveToken(request);

        // 2. validateToken()로 토큰 유효성 검사
        // 유효한 토큰이면 Authentication 객체를 가져와서 SecurityContext에 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰이 유효하면 토큰으로 부터 유저 정보를 받아옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. SecurityContext에 Authentication 객체 저장
            // 이 시점 부터는 Spring Security가 인증된 사용자로 인식
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Security Context에 '{}' 인증 정보를 저장했습니다", authentication.getName());
        }

        // 5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // "Authorization" 헤더가 있고, "Bearer "로 시작하는지 확인
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
