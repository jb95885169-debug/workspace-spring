package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지 전송 요청 (STOMP /app/chat/send)
 * 보낸 사람은 서버가 로그인 정보(Principal)로 정하므로 받지 않는다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSendRequest {

    private int matchId;
    private String messageType;  // TEXT / IMAGE / VIDEO (없으면 TEXT)
    private String content;      // TEXT일 때
    private String fileUrl;      // IMAGE / VIDEO일 때 (/chat/upload가 발급한 주소)
}
