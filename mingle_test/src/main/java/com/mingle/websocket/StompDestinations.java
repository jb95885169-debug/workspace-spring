package com.mingle.websocket;

/**
 * 서버 → 클라이언트 STOMP 주소
 *
 * 서버는 convertAndSendToUser(회원ID, 아래 주소, 데이터)로 보내고,
 * 클라이언트는 앞에 /user를 붙여 구독한다. (예: stompClient.subscribe("/user/queue/chat", ...))
 * 같은 회원이 여러 탭을 열었으면 모든 탭이 받는다.
 */
public final class StompDestinations {

    /** 새 채팅 메시지 (두 참여자 모두에게, 화면에서 matchId로 현재 채팅방인지 확인) */
    public static final String CHAT = "/queue/chat";

    /** 상대가 내 메시지를 읽음 (ChatReadEvent { matchId, readerId }) */
    public static final String CHAT_READ = "/queue/chat-read";

    /** 채팅 목록 항목 갱신 (MatchChatResponse) */
    public static final String CHAT_LIST = "/queue/chat-list";

    /** 상대가 매칭을 취소함 { matchId } (채팅방 화면을 읽기 전용으로) */
    public static final String MATCH_CANCELLED = "/queue/match-cancelled";

    /** 받은 좋아요 (UserResponse) */
    public static final String LIKE = "/queue/like";

    /** 알림 토스트 (NotificationMessage) */
    public static final String NOTIFICATION = "/queue/notification";

    /** STOMP 처리 실패 { message } (보낸 연결에만) */
    public static final String ERRORS = "/queue/errors";

    private StompDestinations() {
    }
}
