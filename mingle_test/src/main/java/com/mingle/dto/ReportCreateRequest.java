package com.mingle.dto;

import lombok.Data;

@Data
public class ReportCreateRequest {

    private long targetUserId;
    private String reason;
    private String title;
    private String content;
}