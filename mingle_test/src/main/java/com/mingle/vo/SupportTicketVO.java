package com.mingle.vo;

import java.util.Date;

import lombok.Data;

/** 회원 문의와 관리자 답변 정보 (mingle_support_tickets). */
@Data
public class SupportTicketVO {

    private long id;
    private long userId;
    private String category;
    private String title;
    private String content;
    private String status;
    private String answer;
    private Date answeredAt;
    private Date createdAt;
}
