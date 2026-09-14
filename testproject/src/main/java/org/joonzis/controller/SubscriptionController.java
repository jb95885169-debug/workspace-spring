package org.joonzis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.service.UserService; // 패키지 경로에 맞게 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {

    @Autowired
    private UserService userService; // ★ 서비스 주입 추가

    @GetMapping("")
    public String subscription() {
        return "subscription/subscription";
    }

    @GetMapping("/history")
    public String paymentHistory() {
        return "subscription/paymentHistory";
    }

    @PostMapping(value = "/pay/process", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> processMockPayment(@RequestBody Map<String, Object> requestMap) {
        try {
            String tier = (String) requestMap.get("tier"); // 'Gold' 또는 'Platinum'
            Number amountNum = (Number) requestMap.get("amount");
            long amount = amountNum != null ? amountNum.longValue() : 0;
            String merchantUid = (String) requestMap.get("merchantUid");
            
            Long userId = 1L; // 임시 테스트용 유저 ID (로그인 유저 ID로 대체)
            
            // 서비스 단에서 결제 정보(Payments), 구독 정보(Subscriptions), 유저 등급을 한 번에 처리
            userService.processSubscriptionPayment(userId, tier, amount, merchantUid);
            
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("fail");
        }
    }
    
    
 // 수정 전: @GetMapping(value = "/subscription/info", ...)
    // 수정 후: 아래처럼 /info 로만 변경
    @GetMapping(value = "/info", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo() {
        Long userId = 1L; // 테스트 유저 ID
        
        SubscriptionVO subInfo = userService.getActiveSubscription(userId);
        List<PaymentVO> paymentList = userService.getPaymentHistory(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("subscription", subInfo);
        result.put("paymentList", paymentList);
        
        return ResponseEntity.ok(result);
    }
}