package org.joonzis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.service.UserService;
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
    private UserService userService;

    @GetMapping("")
    public String subscription() {
        return "subscription/subscription";
    }

    @GetMapping("/history")
    public String paymentHistory() {
        return "subscription/paymentHistory";
    }

    // [수정] 포트원 결제 성공 후 데이터를 받아 처리하는 엔드포인트
    @PostMapping(value = "/pay/process", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> processPayment(@RequestBody Map<String, Object> requestMap) {
        try {
            String tier = (String) requestMap.get("tier"); // 'Gold' 또는 'Platinum'
            Number amountNum = (Number) requestMap.get("amount");
            long amount = amountNum != null ? amountNum.longValue() : 0;
            String merchantUid = (String) requestMap.get("merchantUid");
            String impUid = (String) requestMap.get("impUid"); // ★ 포트원 고유 결제 번호 추가 수신
            
            Long userId = 1L; // 임시 테스트용 유저 ID
            
            // 필요하다면 impUid를 로그로 찍어보거나 결제 검증에 활용할 수 있습니다.
            System.out.println("포트원 결제 승인 완료 - impUid: " + impUid + ", merchantUid: " + merchantUid);
            
            // 기존에 잘 만들어 둔 서비스 호출 (DB에 payments, subscriptions 기록)
            userService.processSubscriptionPayment(userId, tier, amount, merchantUid);
            
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("fail");
        }
    }
    
    @GetMapping(value = "/info", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo() {
        Long userId = 1L;
        
        SubscriptionVO subInfo = userService.getActiveSubscription(userId);
        List<PaymentVO> paymentList = userService.getPaymentHistory(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("subscription", subInfo);
        result.put("paymentList", paymentList);
        
        return ResponseEntity.ok(result);
    }
}