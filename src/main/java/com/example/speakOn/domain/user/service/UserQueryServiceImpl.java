package com.example.speakOn.domain.user.service;

import com.example.speakOn.domain.subscription.repository.SubscriptionRepository;
import com.example.speakOn.domain.user.converter.UserConverter;
import com.example.speakOn.domain.user.dto.UserResponse;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    // User 존재 여부 검증
    @Override
    public Boolean existsUserById(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    // 마이페이지
    @Override
    public UserResponse.MyPageResponseDTO getMyPageInfo(Long userId) {

        // 1. 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 2. 구독 정보 조회
        var activeSubscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now());

        Boolean isSubscribed = activeSubscription.isPresent();
        LocalDateTime subscriptionExpiredAt = activeSubscription
                .map(subscription -> subscription.getExpiredAt())
                .orElse(null);

        // 해지 여부
        Boolean isSubscriptionCancelled = activeSubscription
                .map(subscription -> subscription.getIsCancelled())
                .orElse(false);

        // 3. 응답 DTO 반환
        return UserConverter.toMyPageResponseDTO(user, isSubscribed, subscriptionExpiredAt, isSubscriptionCancelled);
    }

    // 온보딩 완료
    @Override
    @Transactional
    public void completeOnboarding(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        user.completeOnboarding();
    }
}
