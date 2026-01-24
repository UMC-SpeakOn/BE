package com.example.speakOn.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 인증 관련 유틸리티 클래스
 * SecurityContext에서 인증된 사용자 정보를 쉽게 가져올 수 있도록 도와줍니다.
 */
@Slf4j
@Component
public class AuthUtil {

    /**
     * SecurityContext에서 현재 인증된 사용자의 ID를 가져옵니다.
     *
     * @return 인증된 사용자의 ID (Long)
     * @throws RuntimeException 인증 정보가 없거나 유효하지 않은 경우
     */
    public Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                log.error("인증 정보가 존재하지 않습니다.");
                throw new RuntimeException("인증되지 않은 사용자입니다.");
            }

            String userId = authentication.getName();
            log.debug("인증된 사용자 ID: {}", userId);

            return Long.parseLong(userId);

        } catch (NumberFormatException e) {
            log.error("사용자 ID 파싱 실패: {}", e.getMessage());
            throw new RuntimeException("유효하지 않은 사용자 ID 형식입니다.");
        }
    }

    /**
     * 현재 인증된 사용자의 Authentication 객체를 가져옵니다.
     *
     * @return Authentication 객체
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
