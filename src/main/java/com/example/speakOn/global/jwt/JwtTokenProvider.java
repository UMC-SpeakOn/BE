package com.example.speakOn.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
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
    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-expiration}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-expiration}") long refreshTokenValidityInMilliseconds
    ) {
        // 시크릿 키를 바이트 배열로 변환하여 Key 객체 생성
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    // Access Token 생성
    public String generateAccessToken(Long userId) {
        return generateToken(userId, accessTokenValidityInMilliseconds);
    }

    // Refresh Token 생성
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, refreshTokenValidityInMilliseconds);
    }

    // 공통 토큰 생성 메서드
    private String generateToken(Long userId, long expirationTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setIssuer(issuer) // 이슈어
                .setIssuedAt(now) // 발급 시간
                .setExpiration(validity) // 만료 시간
                .setSubject(String.valueOf(userId)) // subject에 userId 설정
                .signWith(key) // 서명에 사용할 키 설정
                .compact();
    }

    // Jwt 토큰으로부터 인증 객체 생성
    public Authentication getAuthentication(String token) {

        // 1. 토큰에서 Claims 추출
        Claims claims = parseClaims(token);

        // 2. 권한 설정
        // 여기서는 간단히 "USER" 권한만 부여n - singletonList 사용
        // 근데 리스트로 주는 이유는 Authentication 인터페이스가 Collection을 요구하기 때문
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        // 3. UserDetails 객체 생성
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 4. Authentication 객체 반환
        // 이미 검증된 토큰이므로 비밀번호는 빈 문자열로 설정
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
