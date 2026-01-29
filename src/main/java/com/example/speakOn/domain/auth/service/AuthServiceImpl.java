package com.example.speakOn.domain.auth.service;

import com.example.speakOn.domain.auth.converter.AuthConverter;
import com.example.speakOn.domain.auth.dto.AuthResponse;
import com.example.speakOn.domain.auth.dto.GoogleDTO;
import com.example.speakOn.domain.auth.dto.KakaoDTO;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.enums.SocialType;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.jwt.JwtTokenProvider;
import com.example.speakOn.global.util.GoogleUtil;
import com.example.speakOn.global.util.KakaoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final KakaoUtil kakaoUtil;
//    private final GoogleUtil googleUtil;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponse.SocialLoginResponseDTO loginWithKakaoCode(String code, String redirectUri) {

        // 1. 인가 코드로 카카오 액세스 토큰 발급
        String kakaoAccessToken = kakaoUtil.getAccessToken(code, redirectUri);

        // 2. 카카오 액세스 토큰으로 유저 정보 조회
        KakaoDTO.UserInfoResponse userInfo = kakaoUtil.getUserInfo(kakaoAccessToken);

        // 3. 회원가입 or 로그인 처리
        User user = registerOrLogin(userInfo, SocialType.KAKAO);

        // 4. JWT 토큰 발급
        String jwtAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().toString());
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthConverter.toSocialLoginResponseDTO(user.getId(), jwtAccessToken, jwtRefreshToken);
    }

//    @Override
//    @Transactional
//    public AuthResponse.SocialLoginResponseDTO loginWithGoogleCode(String code, String redirectUri) {
//
//        // 1. 인가 코드로 구글 액세스 토큰 발급
//        String googleAccessToken = googleUtil.getAccessToken(code, redirectUri);
//
//        // 2. 구글 액세스 토큰으로 유저 정보 조회
//        GoogleDTO.UserInfoResponse userInfo = googleUtil.getUserInfo(googleAccessToken);
//
//        // 3. 회원가입 or 로그인 처리
//        User user = registerOrLogin(userInfo, SocialType.GOOGLE);
//
//        // 4. JWT 토큰 발급
//        String jwtAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getRole().toString());
//        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
//
//        return AuthConverter.toSocialLoginResponseDTO(user.getId(), jwtAccessToken, jwtRefreshToken);
//    }

    private User registerOrLogin(KakaoDTO.UserInfoResponse userInfo, SocialType socialType) {
        String socialId = String.valueOf(userInfo.id());

        return userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElseGet(() -> {
                    User newUser = AuthConverter.toUser(userInfo, socialType);
                    return userRepository.save(newUser);
                });
    }
//
//    private User registerOrLogin(GoogleDTO.UserInfoResponse userInfo, SocialType socialType) {
//        String socialId = userInfo.id();
//
//        return userRepository.findBySocialTypeAndSocialId(socialType, socialId)
//                .orElseGet(() -> {
//                    User newUser = AuthConverter.toUser(userInfo, socialType);
//                    return userRepository.save(newUser);
//                });
//    }
}
