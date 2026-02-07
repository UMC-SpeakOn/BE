package com.example.speakOn.domain.subscription.entity;

import com.example.speakOn.domain.user.entity.User;
import com.example.speakOn.global.apiPayload.code.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long amount;

    // 토스페이먼츠 주문 ID
    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    // 만료일
    private LocalDateTime expiredAt;

    // 토스페이먼츠 결제 키
    private String paymentKey;

    // 결제 완료 시간
    private LocalDateTime approvedAt;

    public static Subscription createSubscription(User user, String orderId, String orderName, Long amount, String paymentKey) {
        LocalDateTime now = LocalDateTime.now();

        return Subscription.builder()
                .user(user)
                .orderId(orderId)
                .orderName(orderName)
                .amount(amount)
                .paymentKey(paymentKey)
                .approvedAt(now)
                .expiredAt(now.plusDays(30)) // 만료기준 : +30일
                .build();
    }
}

