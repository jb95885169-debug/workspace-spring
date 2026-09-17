package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구독 상품 (GET /api/subscriptions/products)
 *
 * price는 정가, payAmount는 이 회원이 지금 실제로 낼 금액이다.
 * 업그레이드는 남은 기간만큼의 차액만 받으므로 둘이 다르다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private int productId;
    private String name;
    private String tier;
    private String description;
    private int price;
    private int durationDays;

    /* 아래는 서비스가 회원 상태를 보고 채운다 (매퍼는 채우지 않음) */

    /** 지금 살 수 있는지 */
    private boolean buyable;

    /** 실제로 낼 금액 (신규 / 연장은 정가, 업그레이드는 차액) */
    private int payAmount;

    /** 화면에 보여 줄 안내 (못 사는 이유 또는 "기간 연장" 같은 설명) */
    private String note;
}
