package org.joonzis.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.joonzis.domain.PaymentVO;
import org.joonzis.domain.SubscriptionVO;
import org.joonzis.domain.SupportTicketVO;

public interface UserMapper {
    // 기존 메서드 (유저 등급 변경용)
    void updateSubscription(@Param("id") Long id, @Param("subscriptionTier") String subscriptionTier);
    
    // 새롭게 추가할 결제 및 구독 관련 메서드들
    void insertPayment(PaymentVO payment);
    void insertSubscription(SubscriptionVO subscription);
    void updateUserTier(Map<String, Object> param);
    List<SupportTicketVO> selectSupportTicketsByUserId(Long userId);
    
    // 현재 활성화된 구독 정보 조회 (가장 최근 만료일 기준)
    SubscriptionVO selectActiveSubscription(Long userId);

    // 결제 내역 리스트 조회
    List<PaymentVO> selectPaymentHistory(Long userId);
    
}