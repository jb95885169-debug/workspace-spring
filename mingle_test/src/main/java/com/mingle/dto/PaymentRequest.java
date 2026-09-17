package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구독 결제 요청 (POST /api/subscriptions/pay)
 *
 * 금액은 받지 않는다. 서버가 상품 가격을 그대로 쓰기 때문에 금액을 조작할 수 없다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private int productId;
}
