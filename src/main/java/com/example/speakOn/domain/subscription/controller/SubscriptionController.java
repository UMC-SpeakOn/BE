package com.example.speakOn.domain.subscription.controller;

import com.example.speakOn.domain.subscription.dto.SubscriptionRequest;
import com.example.speakOn.domain.subscription.dto.SubscriptionResponse;
import com.example.speakOn.domain.subscription.service.SubscriptionService;
import com.example.speakOn.global.apiPayload.ApiResponse;
import com.example.speakOn.global.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subscription API", description = "구독 관련 API")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final AuthUtil authUtil;

    /**
     * 구독 결제 승인
     * 토스페이먼츠에서 전달받은 결제 정보를 확인하고 구독을 생성합니다.
     */
    @PostMapping("/confirm")
    @Operation(summary = "결제 승인", description = "토스페이먼츠 결제를 승인하고 구독을 생성합니다.")
    public ApiResponse<SubscriptionResponse.SubscriptionResponseDto> confirmPayment(
            @Valid @RequestBody SubscriptionRequest.SubscriptionRequestDto request) {

        Long userId = authUtil.getCurrentUserId();
        log.info("결제 승인 요청 - userId: {}, orderId: {}", userId, request.getOrderId());

        SubscriptionResponse.SubscriptionResponseDto response = subscriptionService.successSubscription(userId, request);

        return ApiResponse.onSuccess(response);
    }
}



