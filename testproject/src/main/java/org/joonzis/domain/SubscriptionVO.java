package org.joonzis.domain;

import java.util.Date;

import lombok.Data;
@Data
public class SubscriptionVO {
    private Long id;
    private Long userId;
    private Long productId;
    private Long paymentId;
    private String status;
    private Date startDate;
    private Date endDate;
    private Date cancelledAt;
    private Date createdAt;

 
}