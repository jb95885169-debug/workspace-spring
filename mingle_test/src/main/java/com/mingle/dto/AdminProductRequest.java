package com.mingle.dto;

import lombok.Data;

@Data
public class AdminProductRequest {

    private int price;
    private int durationDays;
    private String description;
    private boolean active;
}