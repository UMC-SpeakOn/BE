package com.example.speakOn.domain.subscription.converter;

import com.example.speakOn.domain.subscription.dto.SubscriptionResponse;
import com.example.speakOn.domain.subscription.entity.Subscription;

public class SubscriptionConverter {

    /**
     * Subscription 엔티티를 SubscriptionResponseDto로 변환
     */
    public static SubscriptionResponse.SubscriptionResponseDto toSubscriptionResponseDto(Subscription subscription) {
        return SubscriptionResponse.SubscriptionResponseDto.builder()
                .subscriptionId(subscription.getId())
                .orderName(subscription.getOrderName())
                .amount(subscription.getAmount())
                .approvedAt(subscription.getApprovedAt())
                .expiredAt(subscription.getExpiredAt())
                .orderId(subscription.getOrderId())
                .message("구독이 완료되었습니다.")
                .build();
    }

    /**
     * 구독 해지 응답 DTO 생성
     */
    public static SubscriptionResponse.CancelSubscriptionResponseDto toCancelSubscriptionResponseDto(Subscription subscription) {
        return SubscriptionResponse.CancelSubscriptionResponseDto.builder()
                .subscriptionId(subscription.getId())
                .cancelledAt(subscription.getCancelledAt())
                .message("구독이 해지되었습니다.")
                .build();
    }
}
