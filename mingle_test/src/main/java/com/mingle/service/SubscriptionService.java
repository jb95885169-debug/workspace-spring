package com.mingle.service;

import java.util.List;

import com.mingle.dto.PaymentHistoryResponse;
import com.mingle.dto.PaymentRequest;
import com.mingle.dto.ProductResponse;
import com.mingle.dto.SubscriptionResponse;

/**
 * 구독 (상품 / 결제 / 등급)
 *
 * 실제 결제 연동은 없다. 결제 버튼을 누르면 결제 내역과 구독을 함께 만든다.
 * 등급은 따로 저장하지 않고, 기간이 유효한 구독의 상품 등급으로 계산한다.
 *
 * 살 수 있는 규칙
 * - 구독이 없으면        : 아무 상품이나 정가로 구매
 * - 같은 등급을 또 사면  : 정가로 기간 연장 (기존 종료일부터 이어 붙임)
 * - 상위 등급으로 올리면 : 같은 기간 상품만, 남은 기간의 차액만 결제 (종료일은 그대로)
 * - 하위 등급은         : 이용이 끝난 뒤에만 구매
 */
public interface SubscriptionService {

    /** 구독이 없을 때의 기본 등급 */
    String BASIC_TIER = "BASIC";

    /** 판매 중인 상품 목록 (회원 상태에 따라 buyable / payAmount가 채워진다) */
    List<ProductResponse> getProducts(int userId);

    /** 지금 내 구독 (없으면 BASIC) */
    SubscriptionResponse getMySubscription(int userId);

    /** 지금 내 등급 (BASIC / GOLD / PLATINUM) */
    String getCurrentTier(int userId);

    /**
     * 결제 (결제 내역 + 구독을 한 트랜잭션으로 만든다)
     * 살 수 없는 상품이면 IllegalArgumentException
     *
     * @return 결제 후의 내 구독
     */
    SubscriptionResponse pay(int userId, PaymentRequest request);

    /** 내 결제 / 구독 내역 (최근 순) */
    List<PaymentHistoryResponse> getPaymentHistory(int userId);
}
