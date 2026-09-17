package com.mingle.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 결제 내역 (mingle_payments)
 *
 * productName과 amount는 결제 시점의 값을 그대로 남긴다.
 * 나중에 상품 이름이나 가격이 바뀌어도 과거 내역은 그대로 보이게 하기 위해서다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVO {

    private int id;
    private int userId;
    private int productId;
    private String productName;
    private int amount;
}
