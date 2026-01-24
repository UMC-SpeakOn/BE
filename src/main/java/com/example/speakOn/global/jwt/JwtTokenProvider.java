package com.example.speakOn.global.jwt;

import com.example.speakOn.global.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final String issuer;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    // application.yml 에서 설정값을 가져와 초기화
    public JwtTokenProvider(JwtProperties jwtProperties) {
        // 시크릿 키를 바이트 배열로 변환하여 Key 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = jwtProperties.getIssuer();
        this.accessTokenValidityInMilliseconds = jwtProperties.getAccessExpiration();
        this.refreshTokenValidityInMilliseconds = jwtProperties.getRefreshExpiration();
    }

    // Access Token 생성
    public String generateAccessToken(Long userId, String role) {
        return generateToken(userId, accessTokenValidityInMilliseconds, "access", role);
    }

    // Refresh Token 생성
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, refreshTokenValidityInMilliseconds, "refresh", null);
    }

    // 공통 토큰 생성 메서드
    private String generateToken(Long userId, long expirationTime, String tokenType, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        JwtBuilder builder = Jwts.builder()
                .setIssuer(issuer) // 이슈어
                .setIssuedAt(now) // 발급 시간
                .setExpiration(validity) // 만료 시간
                .setSubject(String.valueOf(userId)) // subject에 userId 설정
                .claim("category", tokenType); // 토큰 유형 설정

        // Role 정보가 있을 때만 Claims에 추가
        if (role != null) {
            builder.claim("role", role);
        }

        return builder.signWith(key) // 서명에 사용할 키 설정
                .compact();
    }

    // Jwt 토큰으로부터 인증 객체 생성
    public Authentication getAuthentication(String token) {

        // 1. 토큰에서 Claims 추출
        Claims claims = parseClaims(token);

        // ★ 보안 검사: Access Token이 아니면 인증 거부
        String category = (String) claims.get("category");
        if (category == null || !category.equals("access")) {
            throw new RuntimeException("토큰이 유효하지 않습니다. (Access Token이 아닙니다)");
        }

        // 2. 권한 정보 추출 (토큰에 담긴 role을 꺼냄)
        String role = (String) claims.get("role");

        // 혹시 role이 없다면 기본값 "USER" 설정 (Null 방지)
        if (role == null) {
            role = "USER";
        }

        // 3. GrantedAuthority 리스트 생성
        // DB에 "USER"로 저장되어 있어도, 시큐리티는 "ROLE_USER" 형태를 좋아함
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role)
        );

        // 4. UserDetails 객체 생성
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 5. Authentication 객체 반환
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.SignatureException | MalformedJwtException e) { // SecurityException 대신 SignatureException
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 유저 ID 추출
    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
