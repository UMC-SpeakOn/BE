package com.example.speakOn.global.util;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentUtil {

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void confirm(String paymentKey, String orderId, Long amount) {
        HttpHeaders headers = new HttpHeaders();
        // 시크릿 키를 Base64로 인코딩해서 헤더에 넣음 (토스 규칙)
        String encodedAuth = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject params = new JSONObject();
        params.put("paymentKey", paymentKey);
        params.put("orderId", orderId);
        params.put("amount", amount);

        HttpEntity<String> entity = new HttpEntity<>(params.toString(), headers);

        try {
            // 토스 승인 API 호출!
            restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    entity,
                    String.class
            );
        } catch (Exception e) {
            // 실패하면 여기서 에러가 터지고, Service의 트랜잭션이 롤백됨
            throw new RuntimeException("토스 결제 승인 실패: " + e.getMessage());
        }
    }
}
