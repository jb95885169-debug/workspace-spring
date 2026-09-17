package com.mingle.dto;

import java.util.Date;

import com.mingle.vo.ChatMessageVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 응답
 * - GET /api/chats/{matchId}/messages
 * - WebSocket /user/queue/chat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private int id;
    private int matchId;
    private int senderId;
    private String messageType;  // TEXT / IMAGE / VIDEO
    private String content;      // TEXT만
    private String fileUrl;      // IMAGE / VIDEO만
    private int isRead;          // 상대방 읽음 여부 (0 / 1)
    private Date createdAt;

    public static ChatMessageResponse from(ChatMessageVO message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getMatchId(),
                message.getSenderId(),
                message.getMessageType(),
                message.getContent(),
                message.getFileUrl(),
                message.getIsRead(),
                message.getCreatedAt());
    }
}
