package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.mapper.UserMapper;
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
        long productId = tier.equalsIgnoreCase("Gold") ? 1L : 2L;
        String productName = "MIRA " + tier + " 멤버십 (30일)";

        PaymentVO payment = new PaymentVO();
        payment.setUserId(userId);
        payment.setProductId(productId);
        payment.setProductName(productName);
        payment.setAmount(amount);
        userMapper.insertPayment(payment); 

        SubscriptionVO subscription = new SubscriptionVO();
        subscription.setUserId(userId);
        subscription.setProductId(productId);
        subscription.setPaymentId(payment.getId()); 
        userMapper.insertSubscription(subscription);
    }
    
    @Override
    public SubscriptionVO getActiveSubscription(Long userId) {
        return userMapper.selectActiveSubscription(userId);
    }

    @Override
    public List<PaymentVO> selectPaymentHistory(Long userId) {
        return userMapper.selectPaymentHistory(userId);
    }
}