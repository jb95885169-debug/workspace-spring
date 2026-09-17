package com.mingle.restcontroller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.PaymentHistoryResponse;
import com.mingle.dto.PaymentRequest;
import com.mingle.dto.ProductResponse;
import com.mingle.dto.SubscriptionResponse;
import com.mingle.security.LoginUserId;
import com.mingle.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionRestController {

    @Autowired
    private SubscriptionService subscriptionService;


    // 판매 중인 상품 목록
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(@LoginUserId int userId) {

        // 지금 구독 상태에 따라 살 수 있는지와 낼 금액이 달라진다
        return ResponseEntity.ok(subscriptionService.getProducts(userId));
    }


    // 지금 내 구독 (없으면 tier = BASIC)
    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMySubscription(@LoginUserId int userId) {

        return ResponseEntity.ok(subscriptionService.getMySubscription(userId));
    }


    // 결제 (실제 결제 연동 없음, 결제 내역 + 구독 생성)
    @PostMapping("/pay")
    public ResponseEntity<SubscriptionResponse> pay(
            @RequestBody PaymentRequest request,
            @LoginUserId int userId) {

        return ResponseEntity.ok(subscriptionService.pay(userId, request));
    }


    // 내 결제 / 구독 내역
    @GetMapping("/history")
    public ResponseEntity<List<PaymentHistoryResponse>> getPaymentHistory(@LoginUserId int userId) {

        return ResponseEntity.ok(subscriptionService.getPaymentHistory(userId));
    }


    /** 없는 상품 / 판매 중지된 상품 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidProduct(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
}
