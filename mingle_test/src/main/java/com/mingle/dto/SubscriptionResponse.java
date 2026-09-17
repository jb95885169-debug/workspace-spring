package com.mingle.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 현재 구독 (GET /api/subscriptions/me)
 *
 * 유효한 구독이 없으면 tier가 BASIC이고 나머지는 비어 있다.
 * price / durationDays는 업그레이드 차액을 계산할 때 쓴다 (하루 값 = price / durationDays).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private String tier;          // BASIC / GOLD / PLATINUM
    private String productName;
    private String status;        // ACTIVE(자동갱신) / CANCELLED(해지 예약, 종료일까지 이용)
    private Date startDate;
    private Date endDate;

    private int price;            // 이용 중인 상품의 정가
    private int durationDays;     // 이용 중인 상품의 기간

    /** 구독이 없는 기본 상태 */
    public static SubscriptionResponse basic() {
        return new SubscriptionResponse("BASIC", null, null, null, null, 0, 0);
    }
}
