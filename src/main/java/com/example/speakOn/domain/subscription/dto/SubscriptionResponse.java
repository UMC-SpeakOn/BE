package com.example.speakOn.domain.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class SubscriptionResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "구독 결제 승인 응답 DTO")
    public static class SubscriptionResponseDto {

        @Schema(description = "구독 ID", example = "1")
        private Long subscriptionId;

        @Schema(description = "주문명", example = "Conversation Record Plan")
        private String orderName;

        @Schema(description = "결제 금액", example = "3900")
        private Long amount;

        @Schema(description = "구독 시작일", example = "2026-02-07T10:30:00")
        private LocalDateTime approvedAt;

        @Schema(description = "구독 만료일", example = "2026-03-09T10:30:00")
        private LocalDateTime expiredAt;

        @Schema(description = "주문 ID", example = "order_20260207_123456")
        private String orderId;

        @Schema(description = "구독 상태 메시지", example = "구독이 완료되었습니다.")
        private String message;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "구독 해지 응답 DTO")
    public static class CancelSubscriptionResponseDto {

        @Schema(description = "구독 ID", example = "1")
        private Long subscriptionId;

        @Schema(description = "해지 시간", example = "2026-02-08T10:30:00")
        private LocalDateTime cancelledAt;

        @Schema(description = "해지 완료 메시지", example = "구독이 해지되었습니다.")
        private String message;
    }
}
