package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구독 이력 (mingle_subscriptions)
 * 결제 1건에 구독 1건 (uk_subscriptions_payment)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionVO {

    private int id;
    private int userId;
    private int productId;
    private int paymentId;

    /** 이용 시작일 (같은 등급을 또 사면 기존 구독의 종료일부터 이어 붙인다) */
    private Date startDate;

    /** 이용 종료일 (업그레이드는 기존 구독의 종료일을 그대로 쓴다) */
    private Date endDate;
}
