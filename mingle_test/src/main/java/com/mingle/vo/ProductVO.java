package com.mingle.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구독 상품 (mingle_products)
 * BASIC은 무료 기본 등급이라 상품이 없다 (유효한 구독이 없으면 BASIC).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {

    private int id;
    private String name;
    private String tier;          // GOLD / PLATINUM
    private String description;
    private int price;            // 원
    private int durationDays;     // 이용 기간 (일)
    private boolean active;
}
