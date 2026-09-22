package com.mingle.vo;

import java.util.Date;

import lombok.Data;

/** 관리자 신고 처리 정보 (mingle_reports). */
@Data
public class ReportVO {

    private long id;
    private long reporterId;
    private long targetUserId;
    private String targetType;
    private String reporterNickname;
    private String targetNickname;
    private String reason;
    private String title;
    private String content;
    private String status;
    private Date createdAt;
    private Date processedAt;
}
