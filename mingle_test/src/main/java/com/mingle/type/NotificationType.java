package com.mingle.type;

/**
 * 실시간 알림 종류 (JSON에는 "LIKE" / "MATCH" / "CHAT" 문자열로 나감)
 */
public enum NotificationType {

    LIKE,   // 좋아요 / 슈퍼 좋아요 받음
    MATCH,  // 매칭 성사
    CHAT    // 새 채팅 메시지 (채팅방을 보고 있지 않을 때만 토스트로 보여 준다)
}
