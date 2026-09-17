package org.joonzis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String subscription(Model model, HttpSession session) {
        Long userId = 1L; // 임시 테스트용 유저 ID
        
        // DB에서 활성 구독 정보 조회 후 JSP로 전달
        SubscriptionVO activeSubscription = userService.getActiveSubscription(userId);
        model.addAttribute("activeSubscription", activeSubscription);
        
        return "subscription/subscription";
    }

    @GetMapping(value = "/history", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<PaymentVO> paymentHistory(HttpSession session) {
        // 임시 테스트용 유저 ID (로그인 세션 적용 전이라면 1L 사용)
        Long userId = 1L; 
        
        return userService.selectPaymentHistory(userId);
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
    public ResponseEntity<Map<String, Object>> getSubscriptionInfo(HttpSession session) {
        Long userId = 1L; // 임시 유저 ID
        
        // 이미 만들어져 있는 활성 구독 조회 메서드 재사용!
        SubscriptionVO subInfo = userService.getActiveSubscription(userId); 
        
        Map<String, Object> responseMap = new HashMap<>();
        
        int platinumPrice = 39900; // 플래티넘 정가
        int goldPrice = 19900;    // 골드 정가
        int remainingDays = 0;
        int remainingValue = 0;
        int finalPrice = platinumPrice;
        String currentTier = "Basic";

        if (subInfo != null) {
            currentTier = subInfo.getTier(); // 'Gold' 또는 'Platinum'
            remainingDays = subInfo.getRemainingDays() != null ? subInfo.getRemainingDays() : 0;
            
            // ★ 핵심: 현재 골드 등급이고 남은 일수가 0보다 클 때 환산 가치 계산
            if ("Gold".equalsIgnoreCase(currentTier) && remainingDays > 0) {
                // 골드 하루 환산 금액 * 남은 일수 (반올림)
                remainingValue = (int) Math.round((double) goldPrice / 30 * remainingDays);
                finalPrice = platinumPrice - remainingValue;
            }

            responseMap.put("tier", currentTier); 
            responseMap.put("remainingDays", remainingDays); 
            responseMap.put("endDate", subInfo.getEndDate());
        } else {
            responseMap.put("tier", "Basic");
            responseMap.put("remainingDays", 0);
        }
        
        // 프론트(JSP/JS)에서 모달 띄울 때 쓸 수 있도록 계산된 값들을 함께 내려줌
        responseMap.put("remainingValue", remainingValue); // 골드 잔여 환산 가치
        responseMap.put("finalPrice", finalPrice);         // 최종 플래티넘 결제 금액
        
        return ResponseEntity.ok(responseMap);
    }
    

    
    
    
    
    
}