package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;

public interface UserService {
	void processSubscriptionPayment(Long userId, String tier, long amount, String merchantUid);
	// 1. 활성/최신 구독 정보 조회 메서드
	SubscriptionVO getActiveSubscription(Long userId);
    // 결제/구독 내역 조회 메서드 
    public List<PaymentVO> selectPaymentHistory(Long userId);
    

}