package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방 정보 (GET /api/chats/{matchId})
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private int matchId;
    private boolean readOnly;   // 상대가 매칭을 취소해 대화 내용만 볼 수 있음
}
