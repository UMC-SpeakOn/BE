package com.example.speakOn.domain.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SubscriptionRequest {

    @Schema(description = "구독 결제 요청 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionRequestDto {

        @Schema(description = "토스페이먼츠 결제 키", example = "toss_paymentkey_1234567890")
        @NotBlank(message = "paymentKey는 필수입니다.")
        private String paymentKey;

        @Schema(description = "주문 ID", example = "order_1234567890")
        @NotBlank(message = "orderId는 필수입니다.")
        private String orderId;

        @Schema(description = "결제 금액", example = "3900")
        @NotNull(message = "amount는 필수입니다.")
        private Long amount;
    }
}
