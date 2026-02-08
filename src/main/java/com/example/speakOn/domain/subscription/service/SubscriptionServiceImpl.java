package com.example.speakOn.domain.subscription.service;

import com.example.speakOn.domain.subscription.converter.SubscriptionConverter;
import com.example.speakOn.domain.subscription.dto.SubscriptionRequest;
import com.example.speakOn.domain.subscription.dto.SubscriptionResponse;
import com.example.speakOn.domain.subscription.entity.Subscription;
import com.example.speakOn.domain.subscription.repository.SubscriptionRepository;
import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.domain.user.repository.UserRepository;
import com.example.speakOn.global.apiPayload.code.status.ErrorStatus;
import com.example.speakOn.global.apiPayload.exception.handler.ErrorHandler;
import com.example.speakOn.global.util.TossPaymentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

    private final TossPaymentUtil tossPaymentUtil;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    // 결제 금액 고정값
    private final Long SUBSCRIPTION_AMOUNT = 3900L;

    @Transactional
    @Override
    public SubscriptionResponse.SubscriptionResponseDto successSubscription(Long userId, SubscriptionRequest.SubscriptionRequestDto request) {

        // 1. 금액 검증
        if (!SUBSCRIPTION_AMOUNT.equals(request.getAmount())) {
            throw new RuntimeException("결제 금액이 올바르지 않습니다.");
        }

        // 2. 유저 조회 (결제 승인 전에 수행)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 3. 토스페이먼츠 결제 승인 요청
        tossPaymentUtil.confirm(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );


        // 4. 구독 정보 생성
        Subscription subscription = Subscription.createSubscription(
                user,
                request.getOrderId(),
                "Conversation Record Plan",
                request.getAmount(),
                request.getPaymentKey()
        );

        // 5. 구독 정보 저장
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        // 6. 응답 DTO 반환
        return SubscriptionConverter.toSubscriptionResponseDto(savedSubscription);

    }

    @Transactional
    @Override
    public SubscriptionResponse.CancelSubscriptionResponseDto cancelSubscription(Long userId) {

        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.USER_NOT_FOUND));

        // 2. 활성 구독 조회
        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.SUBSCRIPTION_NOT_FOUND));

        // 3. 구독 해지
        subscription.cancel();
        subscriptionRepository.save(subscription);

        log.info("구독 해지 완료 - userId: {}, subscriptionId: {}", userId, subscription.getId());

        // 4. 응답 DTO 반환
        return SubscriptionConverter.toCancelSubscriptionResponseDto(subscription);

    }
}
