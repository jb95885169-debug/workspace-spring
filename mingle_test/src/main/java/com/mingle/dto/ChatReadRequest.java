package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 읽음 처리 요청 (STOMP /app/chat/read)
 * 읽은 사람은 서버가 로그인 정보(Principal)로 정한다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatReadRequest {

    private int matchId;
}
