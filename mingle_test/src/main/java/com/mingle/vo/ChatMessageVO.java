package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mingle_chat_messages 테이블 (메시지 저장용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVO {

    private int id;
    private int matchId;
    private int senderId;
    private String messageType;  // TEXT / IMAGE / VIDEO
    private String content;      // TEXT만 (사진/동영상은 null)
    private String fileUrl;      // IMAGE / VIDEO만
    private int isRead;          // 0 / 1
    private Date createdAt;
}
