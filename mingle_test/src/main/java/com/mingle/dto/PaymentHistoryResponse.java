package com.mingle.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 결제 / 구독 내역 한 줄 (GET /api/subscriptions/history)
 * 상품 이름과 금액은 결제 당시 값이다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryResponse {

    private String productName;
    private int amount;
    private String status;        // PAID / CANCELLED / REFUNDED
    private Date paidAt;
    private Date startDate;
    private Date endDate;
}
