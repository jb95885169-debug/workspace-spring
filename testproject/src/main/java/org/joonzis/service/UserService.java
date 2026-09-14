package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;

public interface UserService {
	void processSubscriptionPayment(Long userId, String tier, long amount, String merchantUid);
	SubscriptionVO getActiveSubscription(Long userId);
    List<PaymentVO> getPaymentHistory(Long userId);
}