package com.mingle.vo;

import java.util.Date;

import lombok.Data;

/** 관리자 신고 처리 정보 (mingle_reports). */
@Data
public class ReportVO {

    private long id;
    private String status;
    private Date processedAt;
}
