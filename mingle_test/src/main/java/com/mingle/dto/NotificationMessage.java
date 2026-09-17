package com.mingle.dto;

import com.mingle.type.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 알림 토스트 (WebSocket /user/queue/notification)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private NotificationType type;  // LIKE / MATCH / CHAT
    private int senderId;           // 알림을 발생시킨 회원 (좋아요를 보낸 / 매칭된 / 메시지를 보낸 상대)
    private String nickname;        // senderId의 닉네임
    private String message;         // 화면에 보여줄 문구

    /** 눌렀을 때 갈 곳 (MATCH / CHAT은 matchId, LIKE는 null) */
    private Integer targetId;
}
