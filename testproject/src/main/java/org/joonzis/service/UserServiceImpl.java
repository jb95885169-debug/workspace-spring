package org.joonzis.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.mapper.UserMapper; // 혹은 각 매퍼 인터페이스
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public void processSubscriptionPayment(Long userId, String tier, long amount, String merchantUid) {
        // 1. 티어에 따른 상품 ID 및 상품명 결정 (Gold: 1, Platinum: 2)
        long productId = tier.equalsIgnoreCase("Gold") ? 1L : 2L;
        String productName = "MIRA " + tier + " 멤버십 (30일)";

        // 2. 결제 정보(mingle_payments) 저장
        PaymentVO payment = new PaymentVO();
        payment.setUserId(userId);
        payment.setProductId(productId);
        payment.setProductName(productName);
        payment.setAmount(amount);
        // 필요시 merchantUid를 담을 필드가 있다면 세팅
        userMapper.insertPayment(payment); 
        // 주의: MyBatis selectKey를 사용했다면 이 시점에 payment.id에 PK가 담깁니다.

        // 3. 구독 이력(mingle_subscriptions) 저장
        SubscriptionVO subscription = new SubscriptionVO();
        subscription.setUserId(userId);
        subscription.setProductId(productId);
        subscription.setPaymentId(payment.getId()); // 방금 생성된 결제 ID 매핑
        userMapper.insertSubscription(subscription);


        
    }
    
    @Override
    public SubscriptionVO getActiveSubscription(Long userId) {
        return userMapper.selectActiveSubscription(userId);
    }

    @Override
    public List<PaymentVO> getPaymentHistory(Long userId) {
        return userMapper.selectPaymentHistory(userId);
    }
}