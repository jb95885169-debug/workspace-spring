package com.mingle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 구독 화면
 * 상품 목록 / 현재 등급 / 결제 내역은 화면에서 /api/subscriptions로 불러온다.
 */
@Controller
@RequestMapping("/subscription")
public class SubscriptionController {

    @GetMapping
    public String subscriptionPage() {

        return "subscription/subscription";
    }
}
