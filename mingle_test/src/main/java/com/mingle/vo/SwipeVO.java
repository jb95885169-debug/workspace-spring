package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwipeVO {

    private int id;
    private int swiperId;
    private int targetId;
    private String action;
    private Date createdAt;
}