package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchVO {

    private int id;
    private int user1Id;
    private int user2Id;
    private String status;
    private Date matchedAt;
    private Integer cancelledBy;
    private Date cancelledAt;
    private String cancelReason;
}