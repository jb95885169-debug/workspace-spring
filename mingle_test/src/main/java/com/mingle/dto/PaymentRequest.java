package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구독 결제 요청 (POST /api/subscriptions/pay)
 *
 * 금액은 받지 않는다. 서버가 상품 가격과 PortOne 결제 금액을 대조한다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private int productId;
    private String paymentId;
    private String impUid;
    private String merchantUid;
}
