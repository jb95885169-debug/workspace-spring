package org.joonzis.domain;

import java.util.Date;
import lombok.Data;

@Data 
public class PaymentVO {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private Long amount;
    private String status;
    private Date paidAt;
    
    private String startDate;
    private String endDate;
    private String payMethod;
    
}