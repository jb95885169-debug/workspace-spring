package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 읽음 이벤트 (WebSocket /user/queue/chat-read)
 * 메시지를 보낸 상대에게 "readerId가 matchId 채팅방 메시지를 읽었다"고 알린다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadEvent {

    private int matchId;
    private int readerId;
}
