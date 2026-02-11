package com.example.speakOn.domain.subscription.service;

import com.example.speakOn.domain.subscription.dto.SubscriptionRequest;
import com.example.speakOn.domain.subscription.dto.SubscriptionResponse;

public interface SubscriptionService {

    SubscriptionResponse.SubscriptionResponseDto successSubscription(Long userId, SubscriptionRequest.SubscriptionRequestDto request);

    /**
     * 구독 해지
     */
    SubscriptionResponse.CancelSubscriptionResponseDto cancelSubscription(Long userId);

}
