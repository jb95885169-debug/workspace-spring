package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 결제 직전 서버가 계산한 구매 가능 여부와 실제 결제 금액. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePolicyResponse {

    private int productId;
    private String currentTier;
    private boolean buyable;
    private int payAmount;
    private int remainingDays;
    private int remainingValue;
    private String note;
}